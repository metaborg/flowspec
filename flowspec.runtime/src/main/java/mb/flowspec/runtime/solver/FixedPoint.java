package mb.flowspec.runtime.solver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.metaborg.util.Ref;
import org.metaborg.util.iterators.PeekingIterator;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import mb.flowspec.controlflow.IBasicBlock;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.IControlFlowGraph;
import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.controlflow.TransferFunctionAppl;
import mb.flowspec.graph.Algorithms;
import mb.flowspec.runtime.interpreter.InterpreterBuilder;
import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.interpreter.UnreachableException;
import mb.flowspec.runtime.lattice.CompleteLattice.Flipped;
import mb.flowspec.runtime.lattice.FullSetLattice;
import mb.flowspec.runtime.lattice.Lattice;

public class FixedPoint {
    private static final ILogger logger = LoggerUtils.logger(FixedPoint.class);
    private static final String ARTIFICIAL_PROPERTY = "__START__";
    // TODO: Turn this into a config variable
    private static final int FIXPOINT_LIMIT = 500;
    private static final Function<IBasicBlock, IBasicBlock> ID = b -> b;

    private IFlowSpecSolution solution;
    private final FixedPoint.TimingInfo timingInfo;
    private Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> preProperties;
    private Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> postProperties;

    public FixedPoint() {
        this.preProperties = new HashMap<>();
        this.postProperties = new HashMap<>();
        this.timingInfo = new FixedPoint.TimingInfo();
    }

    public IFlowSpecSolution entryPoint(ITermFactory termFactory, IFlowSpecSolution nabl2solution,
        InterpreterBuilder interpBuilder) {
        StaticInfo staticInfo = interpBuilder.build(termFactory, nabl2solution, preProperties);
        return entryPoint(nabl2solution, staticInfo, staticInfo.metadata.keySet());
    }

    public IFlowSpecSolution entryPoint(ITermFactory termFactory, IFlowSpecSolution oldSolution,
        InterpreterBuilder interpBuilder, Collection<String> propNames) {
        StaticInfo staticInfo = interpBuilder.build(termFactory, oldSolution, preProperties);
        return entryPoint(oldSolution, staticInfo, propNames);
    }

    public IFlowSpecSolution entryPoint(IFlowSpecSolution oldSolution, StaticInfo staticInfo,
        Collection<String> propNames) {
        this.solution = oldSolution;
        final IControlFlowGraph cfg = solution.controlFlowGraph();
        preProperties.putAll(solution.preProperties());
        postProperties.putAll(solution.postProperties());

        timingInfo.recordStart();

        timingInfo.recordInterpInit();

        if(logger.debugEnabled()) {
            logger.debug("SCCs: {}", cfg.topoSCCs());
        }

        try {
            solve(cfg, staticInfo, propNames);

            timingInfo.recordEnd();
            timingInfo.logReport(logger);

            IFlowSpecSolution flowspecSolution =
                oldSolution.withProperties(Collections.unmodifiableMap(this.preProperties), Collections.unmodifiableMap(this.postProperties));

            return flowspecSolution;
        } catch(UnimplementedException | UnreachableException | ParseException | CyclicGraphException
            | FixedPointLimitException e) {
            logger.error("Exception during FlowSpec solving: ", e);

            return oldSolution;
        }
    }

    @SuppressWarnings("unchecked") private void solve(IControlFlowGraph cfg, StaticInfo staticInfo,
        Collection<String> propNames) throws CyclicGraphException, FixedPointLimitException {
        // Check that all given property names exists, and meanwhile check if this is the full list of property names
        Set<String> allProps = new HashSet<>(staticInfo.metadata.keySet());
        for(Iterator<String> iterator = propNames.iterator(); iterator.hasNext();) {
            String propName = iterator.next();
            if(!allProps.contains(propName)) {
                logger.error("Given property {} cannot be found and will be ignored. ", propName);
                iterator.remove();
            } else {
                allProps.remove(propName);
            }
        }
        Iterable<String> propTopoOrder;
        // If the full list of property names was given, we can simply find the topo order on the full dependency graph
        if(allProps.isEmpty()) {
            propTopoOrder = Algorithms.topoSort(propNames, staticInfo.dependsOn.inverse());
        } else {
            propTopoOrder = Algorithms.topoDeps(propNames, staticInfo.dependsOn);
        }

        timingInfo.recordReverseTopo();

        for(String prop : propTopoOrder) {
            // remove artificial start used earlier to include all properties in the dependency graph
            if(prop != ARTIFICIAL_PROPERTY) {
                solveFlowSensitiveProperty(cfg, prop, (Metadata<IStrategoTerm>) staticInfo.metadata.get(prop));
                timingInfo.recordProperty(prop);
            }
        }
    }

    private void swapPrePostProperties(String prop, Map<ICFGNode, Ref<IStrategoTerm>> preProps, Map<ICFGNode, Ref<IStrategoTerm>> postProps) {
        this.preProperties.put(prop, postProps);
        this.postProperties.put(prop, preProps);
    }

    private void solveFlowSensitiveProperty(IControlFlowGraph cfg, String prop, Metadata<IStrategoTerm> metadata)
        throws FixedPointLimitException {
        // Phase 1: initialisation
        final Map<ICFGNode, Ref<IStrategoTerm>> preProps = new HashMap<>();
        preProperties.put(prop, preProps);
        final Map<ICFGNode, Ref<IStrategoTerm>> postProps = new HashMap<>();
        postProperties.put(prop, postProps);
        // 1.1: direction
        final Iterable<Set<IBasicBlock>> sccs;
        final Set<ICFGNode> initialNodes;
        final Function<IBasicBlock, Set<IBasicBlock>> next;
        final Function<IBasicBlock, Set<IBasicBlock>> prev;
        final Function<IBasicBlock, IBasicBlock> blockDir;
        switch(metadata.dir) {
            case Forward: {
                blockDir = ID;
                sccs = cfg.topoSCCs();
                initialNodes = cfg.startNodes();
                prev = cfg::prevBlocks;
                next = cfg::nextBlocks;
                break;
            }
            case Backward: {
                blockDir = IBasicBlock::inverse;
                sccs = cfg.revTopoSCCs();
                initialNodes = cfg.endNodes();
                prev = b -> cfg.nextBlocks(b.inverse());
                next = b -> cfg.prevBlocks(b.inverse());
                break;
            }
            default:
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }

        // 1.2: initial values bottom, while finding ignorable nodes (nodes without TF and only 1 predecessor)
        for(Set<IBasicBlock> scc : sccs) {
            for(IBasicBlock b : scc) {
                b.clearIgnored();
                final IBasicBlock block = blockDir.apply(b);
                final Iterator<ICFGNode> iterator = block.iterator();
                ICFGNode n = iterator.next();
                final Set<IBasicBlock> prevBlocks = prev.apply(block);
                if(prevBlocks.size() == 1) {
                    final ICFGNode pred = blockDir.apply(prevBlocks.iterator().next()).last();
                    perhapsIgnored(prop, metadata, block, n, pred, preProps, postProps);
                } else {
                    preProps.put(n, new Ref<>(metadata.lattice.bottom()));
                    postProps.put(n, new Ref<>(metadata.lattice.bottom()));
                }
                for(; iterator.hasNext();) {
                    final ICFGNode pred = n; // definitely only one predecessor
                    n = iterator.next();
                    perhapsIgnored(prop, metadata, block, n, pred, preProps, postProps);
                }
            }
        }

        // 1.3: initial node values
        for(ICFGNode n : initialNodes) {
            preProps.get(n).set(callTFInitial(prop, metadata, n, preProps));
        }

        // Phase 2: Fixpoint iteration
        for(Set<IBasicBlock> scc : sccs) {
            boolean done = false;
            int fixpointCount = 0;
            while(!done) {
                if(fixpointCount >= FIXPOINT_LIMIT) {
                    throw new FixedPointLimitException(prop, FIXPOINT_LIMIT);
                }
                done = true;
                fixpointCount++;
                for(IBasicBlock b : scc) {
                    final IBasicBlock block = blockDir.apply(b);
                    for(PeekingIterator<ICFGNode> iterator = new PeekingIterator<>(block.iterator()); iterator
                        .hasNext();) {
                        final ICFGNode from = iterator.next();
                        if(iterator.hasNext()) {
                            if(compute(from, iterator.peek(), prop, metadata, block, preProps)) {
                                done = false;
                            }
                        } else {
                            final Set<IBasicBlock> nextBlocks = next.apply(block);
                            for(IBasicBlock nextB : nextBlocks) {
                                IBasicBlock nextBlock = blockDir.apply(nextB);
                                if(compute(from, nextBlock.first(), prop, metadata, nextBlock, preProps)
                                    && scc.contains(nextB)) {
                                    done = false;
                                }
                            }
                        }
                    }
                }
            }
            if(fixpointCount > 1) {
                logger.debug("Property '{}' took {} runs through an SCC to solve it. ", prop, fixpointCount);
            }
        }

        // Phase 3: Result calculation
        for(Set<IBasicBlock> scc : sccs) {
            for(IBasicBlock block : scc) {
                for(ICFGNode n : blockDir.apply(block)) {
//                    if(!block.ignored(n)) {
                        IStrategoTerm value = callTF(prop, metadata, n, preProps);
                        postProps.put(n, new Ref<>(value));
//                    }
                }
            }
        }
        switch(metadata.dir) {
            case Forward: {
                break;
            }
            case Backward: {
                swapPrePostProperties(prop, preProps, postProps);
                break;
            }
            default:
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
    }

    public void perhapsIgnored(String prop, Metadata<IStrategoTerm> metadata, IBasicBlock block,
        ICFGNode n, ICFGNode pred, Map<ICFGNode, Ref<IStrategoTerm>> preProps, Map<ICFGNode, Ref<IStrategoTerm>> postProps) {
        if(solution.getTFAppl(pred, prop) == null) {
            block.ignore(n);
            preProps.put(n, preProps.get(pred));
            postProps.put(n, postProps.get(pred));
        } else {
            preProps.put(n, new Ref<>(metadata.lattice.bottom()));
            postProps.put(n, new Ref<>(metadata.lattice.bottom()));
        }
    }

    /**
     * If {@code to} is not ignored, compute the new value for {@code to} from {@code from}'s transfer function. Join
     * the new value and old value of {@code to}.
     * @param preProps 
     * 
     * @return true if there {@code to} was given a new value
     */
    public boolean compute(ICFGNode from, ICFGNode to, String prop, Metadata<IStrategoTerm> metadata,
        IBasicBlock block, Map<ICFGNode, Ref<IStrategoTerm>> preProps) {
        if(!block.ignored(to)) {
            IStrategoTerm afterFromTF = callTF(prop, metadata, from, preProps);
            IStrategoTerm beforeToTF = preProps.get(to).get();
            if(metadata.lattice.nleq(afterFromTF, beforeToTF)) {
                preProps.get(to).set(metadata.lattice.lub(beforeToTF, afterFromTF));
                return true;
            }
        }
        return false;
    }

    /*
     * The bottom value of a MustSet is a symbolic largest set. It's contents cannot be inspected. Therefore when a
     * MustSet analysis is missing a rule for one of the extremal values of the graph, the bottom becomes visible. We
     * patch that here with the empty set. Ugly, but better than RuntimeExceptions. To be replaced with a proper static
     * analysis in FlowSpec.
     */
    private IStrategoTerm callTFInitial(String prop, Metadata<?> metadata, ICFGNode node, Map<ICFGNode, Ref<IStrategoTerm>> preProps) {
        TransferFunctionAppl tfAppl = solution.getTFAppl(node, prop);
        if(tfAppl == null) {
            Lattice<?> l = metadata.lattice;
            if(l instanceof Flipped) {
                l = ((Flipped) l).wrapped;
            }
            if(l instanceof FullSetLattice) {
                return new mb.flowspec.runtime.interpreter.values.Set<>();
            } else {
                throw new RuntimeException("Missing extremal (start/end) rule for node: " + node);
            }
        }
        return callTF(prop, metadata, node, preProps);
    }

    private IStrategoTerm callTF(String prop, Metadata<?> metadata, ICFGNode node, Map<ICFGNode, Ref<IStrategoTerm>> preProps) {
        TransferFunctionAppl tfAppl = solution.getTFAppl(node, prop);
        if(tfAppl == null) {
            return preProps.get(node).get();
        }
        TransferFunction tf = TransferFunction.findFunction(metadata.transferFunctions, tfAppl);
        return tf.call(tfAppl, node);
    }

    protected static class TimingInfo {
        private LinkedHashMap<String, Long> property;
        private long start;
        private long interpInit;
        private long reverseTopo;
        private long end;

        public TimingInfo() {
            this.property = new LinkedHashMap<>();
        }

        public void recordStart() {
            this.start = System.nanoTime();
        }

        public void logReport(ILogger logger) {
            long total = millisecondBetween(this.start, this.end);
            long interpInit = millisecondBetween(this.start, this.interpInit);
            long reverseTopo = millisecondBetween(this.interpInit, this.reverseTopo);
            long properties = millisecondBetween(this.reverseTopo, this.end);
            String[] propertyNames = new String[property.size()];
            long[] propertyTimes = new long[property.size()];
            long current = this.reverseTopo;
            {
                int i = 0;
                for(Entry<String, Long> e : this.property.entrySet()) {
                    propertyNames[i] = e.getKey();
                    propertyTimes[i] = millisecondBetween(current, e.getValue());
                    current = e.getValue();
                    i++;
                }
            }

            StringBuilder message = new StringBuilder();
            message.append("FlowSpec dataflow solver timing report, time in milliseconds.\n");
            message.append("Total time: " + total + "\n");
            message.append("|- Initialising the interpreter: " + interpInit + "\n");
            message.append("|- Reverse topo order of properties: " + reverseTopo + "\n");
            message.append("|- Total dataflow property computation: " + properties + "\n");
            for(int i = 0; i < propertyNames.length; i++) {
                message.append("  |- Compute property '" + propertyNames[i] + "': " + propertyTimes[i] + "\n");
            }

            // TODO: make log level info available through config
            logger.debug(message.toString());
        }

        public void recordInterpInit() {
            this.interpInit = System.nanoTime();
        }

        public void recordReverseTopo() {
            this.reverseTopo = System.nanoTime();
        }

        public void recordProperty(String name) {
            this.property.put(name, System.nanoTime());
        }

        public void recordEnd() {
            this.end = System.nanoTime();
        }

        private static long millisecondBetween(long start, long end) {
            return (end - start) / 1_000_000;
        }
    }
}
