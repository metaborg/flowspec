package mb.flowspec.runtime.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.metaborg.util.Ref;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set.Immutable;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.ICompleteControlFlowGraph;
import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.controlflow.TransferFunctionAppl;
import mb.flowspec.graph.Algorithms;
import mb.flowspec.runtime.interpreter.InterpreterBuilder;
import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.interpreter.UnreachableException;
import mb.flowspec.runtime.lattice.CompleteLattice.Flipped;
import mb.flowspec.runtime.lattice.FullSetLattice;
import mb.flowspec.runtime.lattice.Lattice;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public class FixedPoint {
    private static final ILogger logger = LoggerUtils.logger(FixedPoint.class);
    private static final String ARTIFICIAL_PROPERTY = "__START__";
    // TODO: Turn this into a config variable
    private static final int FIXPOINT_LIMIT = 500;

    private IFlowSpecSolution solution;
    private final FixedPoint.TimingInfo timingInfo;
    private final Map.Transient<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties;
    private final Map.Transient<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties;

    public FixedPoint() {
        this.preProperties = Map.Transient.of();
        this.postProperties = Map.Transient.of();
        this.timingInfo = new FixedPoint.TimingInfo();
    }

    public IFlowSpecSolution entryPoint(IFlowSpecSolution nabl2solution, InterpreterBuilder interpBuilder) {
        StaticInfo staticInfo = interpBuilder.build(nabl2solution, preProperties);
        return entryPoint(nabl2solution, staticInfo, staticInfo.metadata().keySet());
    }

    public IFlowSpecSolution entryPoint(IFlowSpecSolution nabl2solution, InterpreterBuilder interpBuilder, Collection<String> propNames) {
        StaticInfo staticInfo = interpBuilder.build(nabl2solution, preProperties);
        return entryPoint(nabl2solution, staticInfo, propNames);
    }

    public IFlowSpecSolution entryPoint(IFlowSpecSolution nabl2solution, StaticInfo staticInfo, Collection<String> propNames) {
        this.solution = nabl2solution;
        final ICompleteControlFlowGraph.Immutable cfg = solution.controlFlowGraph();
        preProperties.__putAll(solution.preProperties());
        postProperties.__putAll(solution.postProperties());

        if (!cfg.deadEndNodes().isEmpty()) {
            logger.error("Found dead ends in control flow graph: " + cfg.deadEndNodes());
        }

        timingInfo.recordStart();

        timingInfo.recordInterpInit();

        logger.debug("SCCs:" + cfg.topoSCCs());

        try {
            solve(cfg, staticInfo, propNames);

            timingInfo.recordEnd();
            timingInfo.logReport(logger);

            IFlowSpecSolution flowspecSolution = nabl2solution
                    .withPreProperties(this.preProperties.freeze())
                    .withPostProperties(this.postProperties.freeze());

            return flowspecSolution;
        } catch (UnimplementedException | UnreachableException | ParseException | CyclicGraphException | FixedPointLimitException e) {
            logger.error("Exception during FlowSpec solving: ", e);

            return nabl2solution;
        }
    }

    @SuppressWarnings("unchecked")
    private void solve(ICompleteControlFlowGraph.Immutable cfg, StaticInfo staticInfo, Collection<String> propNames)
            throws CyclicGraphException, FixedPointLimitException {
        // Check that all given property names exists, and meanwhile check if this is the full list of property names
        Set<String> allProps = new HashSet<>(staticInfo.metadata().keySet());
        for(String propName : propNames) {
            if(!allProps.contains(propName)) {
                logger.warn("Given property {} cannot be found", propName);
            } else {
                allProps.remove(propName);
            }
        }
        Iterable<String> propTopoOrder;
        // If the full list of property names was given, we can simply find the topo order on the full dependency graph
        if(allProps.isEmpty()) {
            propTopoOrder = Algorithms.topoSort(propNames, staticInfo.dependsOn().inverse());
        } else {
            propTopoOrder = Algorithms.topoDeps(propNames, staticInfo.dependsOn());
        }

        timingInfo.recordReverseTopo();

        for (String prop : propTopoOrder) {
            // remove artificial start used earlier to include all properties in the dependency graph
            if(prop != ARTIFICIAL_PROPERTY) {
                solveFlowSensitiveProperty(cfg, prop, (Metadata<IStrategoTerm>) staticInfo.metadata().get(prop));
                timingInfo.recordProperty(prop);
            }
        }
    }

    private void setProperty(ICFGNode n, String prop, Ref<IStrategoTerm> value) {
        this.preProperties.__put(ImmutableTuple2.of(n, prop), value);
    }

    private void setPostProperty(ICFGNode n, String prop, IStrategoTerm value) {
        this.postProperties.__put(ImmutableTuple2.of(n, prop), new Ref<>(value));
    }

    private void setProperty(ICFGNode n, String prop, IStrategoTerm value) {
        getPropertyRef(n, prop).set(value);
    }

    private IStrategoTerm getProperty(ICFGNode n, String prop) {
        return getPropertyRef(n, prop).get();
    }

    private Ref<IStrategoTerm> getPropertyRef(ICFGNode n, String prop) {
        return this.preProperties.get(ImmutableTuple2.of(n, prop));
    }

    private void solveFlowSensitiveProperty(ICompleteControlFlowGraph.Immutable cfg,
            String prop, Metadata<IStrategoTerm> metadata) throws FixedPointLimitException {
        // Phase 1: initialisation

        final IStrategoTerm bottom = metadata.lattice().bottom();
        final BinaryRelation<ICFGNode, ICFGNode> edges;
        final Iterable<Set<ICFGNode>> sccs;
        final Set<ICFGNode> initNodes;
        switch (metadata.dir()) {
            case Forward: {
                initNodes = cfg.startNodes();
                edges = cfg.edges();
                sccs = cfg.topoSCCs();
                break;
            }
            case Backward: {
                initNodes = cfg.endNodes();
                edges = cfg.edges().inverse();
                sccs = cfg.revTopoSCCs();
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
        for (ICFGNode n : initNodes) {
            setProperty(n, prop, new Ref<>(callTFInitial(prop, metadata, n)));
        }
        final Set<ICFGNode> ignoredNodes = new HashSet<>();
        StreamSupport.stream(sccs.spliterator(), false)
            .flatMap(set -> StreamSupport.stream(set.spliterator(), false))
            .forEach(n -> {
                Ref<IStrategoTerm> ref = perhapsIgnore(n, edges, ignoredNodes, prop)
                        .orElseGet(() -> new Ref<>(bottom));
                setProperty(n, prop, ref);
            });
        logger.debug("Ignoring {} out of {} nodes", ignoredNodes.size(), cfg.nodes().size());

        // Phase 2: Fixpoint iteration
        for(Set<ICFGNode> scc : sccs) {
            boolean done = false;
            int fixpointCount = 0;
            while (!done) {
                if (fixpointCount >= FIXPOINT_LIMIT) {
                    throw new FixedPointLimitException(prop, FIXPOINT_LIMIT);
                }
                done = true;
                fixpointCount++;
                for (ICFGNode from : scc) {
                    for (ICFGNode to : edges.get(from)) {
                        if (!ignoredNodes.contains(to)) {
                            IStrategoTerm afterFromTF = callTF(prop, metadata, from);
                            IStrategoTerm beforeToTF = getProperty(to, prop);
                            if (metadata.lattice().nleq(afterFromTF, beforeToTF)) {
                                setProperty(to, prop, metadata.lattice().lub(beforeToTF, afterFromTF));
                                if (scc.contains(to)) {
                                    done = false;
                                }
                            }
                        }
                    }
                }
            }
            if (fixpointCount > 1) {
                logger.debug("Property '" + prop + "' took " + fixpointCount + " runs through an SCC to solve it. ");
            }
        }

        // Phase 3: Result calculation
        switch (metadata.dir()) {
            case Forward: {
                StreamSupport.stream(sccs.spliterator(), false)
                .flatMap(set -> StreamSupport.stream(set.spliterator(), false))
                .forEach(n -> {
                    IStrategoTerm value = callTF(prop, metadata, n);
                    setPostProperty(n, prop, value);
                });
                break;
            }
            case Backward: {
                HashMap<ICFGNode, IStrategoTerm> temp = new HashMap<>();
                StreamSupport.stream(sccs.spliterator(), false)
                .flatMap(set -> StreamSupport.stream(set.spliterator(), false))
                .forEach(n -> {
                    setPostProperty(n, prop, getProperty(n, prop));
                    IStrategoTerm value = callTF(prop, metadata, n);
                    temp.put(n, value);
                });
                temp.forEach((n, value) -> {
                    setProperty(n, prop, value);
                });
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
    }

    private Optional<Ref<IStrategoTerm>> perhapsIgnore(ICFGNode n, BinaryRelation<ICFGNode, ICFGNode> edges, Set<ICFGNode> ignoredNodes, String prop) {
        Immutable<ICFGNode> predecessors = edges.inverse().get(n);
        if(predecessors.size() == 1) {
            ICFGNode pred = predecessors.iterator().next();
            if(solution.getTFAppl(pred, prop) == null) {
                ignoredNodes.add(n);
                return Optional.of(getPropertyRef(pred, prop));
            }
        }
        return Optional.empty();
    }

    /*
     * The bottom value of a MustSet is a symbolic largest set. It's contents cannot be inspected.
     * Therefore when a MustSet analysis is missing a rule for one of the extremal values of the
     * graph, the bottom becomes visible. We patch that here with the empty set. Ugly, but better
     * than RuntimeExceptions. To be replaced with a proper static analysis in FlowSpec. 
     */
    private IStrategoTerm callTFInitial(String prop, Metadata<?> metadata, ICFGNode node) {
        TransferFunctionAppl tfAppl = solution.getTFAppl(node, prop);
        if (tfAppl == null) {
            Lattice<?> l = metadata.lattice();
            if(l instanceof Flipped) {
                l = ((Flipped) l).wrapped;
            }
            if(l instanceof FullSetLattice) {
                return new mb.flowspec.runtime.interpreter.values.Set<>();
            } else {
                throw new RuntimeException("Missing extremal (start/end) rule for node: " + node);
            }
        }
        return callTF(prop, metadata, node);
    }

    private IStrategoTerm callTF(String prop, Metadata<?> metadata, ICFGNode node) {
        TransferFunctionAppl tfAppl = solution.getTFAppl(node, prop);
        if (tfAppl == null) {
            return getProperty(node, prop);
        }
        TransferFunction tf = TransferFunction.findFunction(metadata.transferFunctions(), tfAppl);
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
                for (Entry<String, Long> e : this.property.entrySet()) {
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
            for (int i = 0; i < propertyNames.length; i++) {
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
