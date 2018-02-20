package meta.flowspec.java.solver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.ICompleteControlFlowGraph;
import org.metaborg.meta.nabl2.controlflow.terms.IFlowSpecSolution;
import org.metaborg.meta.nabl2.controlflow.terms.TransferFunctionAppl;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.IStringTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.util.ImmutableTuple2;
import org.metaborg.meta.nabl2.util.Tuple2;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import meta.flowspec.graph.Algorithms;
import meta.flowspec.java.interpreter.ImmutableInitValues;
import meta.flowspec.java.interpreter.InitValues;
import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.java.interpreter.UnreachableException;

public class FixedPoint {
    private static final ILogger logger = LoggerUtils.logger(FixedPoint.class);
    private static final String ARTIFICIAL_PROPERTY = "__START__";
    private static FixedPoint.TimingInfo timingInfo;
    // TODO: Make this into a config variable
    private static final int FIXPOINT_LIMIT = 10_000;

    private IFlowSpecSolution<CFGNode> solution;
    private final Map.Transient<Tuple2<TermIndex, String>, ITerm> preProperties;
    private final Map.Transient<Tuple2<TermIndex, String>, ITerm> postProperties;
    
    public FixedPoint() {
        this.preProperties = Map.Transient.of();
        this.postProperties = Map.Transient.of();
    }

    public ISolution entryPoint(ISolution nabl2solution, TFFileInfo tfFileInfo) {
        this.solution = nabl2solution.flowSpecSolution();
        final ICompleteControlFlowGraph<CFGNode> cfg = solution.controlFlowGraph();
        preProperties.__putAll(solution.preProperties());
        postProperties.__putAll(solution.postProperties());

        timingInfo = new FixedPoint.TimingInfo();

        /* Pass the NaBL2 solution to the interpreter AST so it can save references to the CFG and the
         * resolution result in certain places
         */
        InitValues initValues = ImmutableInitValues.of(nabl2solution.config(), cfg, this.preProperties,
                                                       nabl2solution.scopeGraph(), nabl2solution.unifier(),
                                                       nabl2solution.astProperties())
                .withNameResolutionCache(nabl2solution.nameResolutionCache());
        tfFileInfo.init(initValues);

        timingInfo.recordInterpInit();

        Iterable<CFGNode> unreachable = cfg.unreachableNodes();
        if (!unreachable.iterator().hasNext()) {
            logger.warn("Found unreachable CFG nodes: " + unreachable);
        }

        logger.debug("SCCs:" + cfg.topoSCCs());

        try {
            solve(cfg, tfFileInfo);

            timingInfo.recordEnd();

            // TODO: add config to turn on timing info
            if (true) {
                timingInfo.logReport(logger);
            }

            return this.flowspecCopyProperties(nabl2solution);
        } catch (UnimplementedException | UnreachableException | ParseException | CyclicGraphException | FixedPointLimitException e) {
            logger.error(e.getMessage());

            return nabl2solution;
        }
    }

    @SuppressWarnings("unchecked")
    private void solve(ICompleteControlFlowGraph<CFGNode> cfg, TFFileInfo tfFileInfo)
            throws CyclicGraphException, FixedPointLimitException {
        Iterable<String> propTopoOrder = Algorithms.topoSort(tfFileInfo.metadata().keySet(), tfFileInfo.dependsOn().inverse());

        timingInfo.recordReverseTopo();

        for (String prop : propTopoOrder) {
            // remove artificial start used earlier to include all properties in the dependency graph
            if(prop != ARTIFICIAL_PROPERTY) {
                solveFlowSensitiveProperty(cfg, prop, (Metadata<ITerm>) tfFileInfo.metadata().get(prop));
                timingInfo.recordProperty(prop);
            }
        }
    }

    private void setPostProperty(CFGNode n, String prop, ITerm value) {
        this.preProperties.__put(ImmutableTuple2.of(TermIndex.get(n).get(), prop), value);
    }

    private void setProperty(CFGNode n, String prop, ITerm value) {
        this.preProperties.__put(ImmutableTuple2.of(TermIndex.get(n).get(), prop), value);
    }

    private ITerm getProperty(CFGNode n, String prop) {
        return this.preProperties.get(ImmutableTuple2.of(TermIndex.get(n).get(), prop));
    }

    @SuppressWarnings("unchecked")
    private void solveFlowSensitiveProperty(ICompleteControlFlowGraph<CFGNode> cfg,
            String prop, Metadata<ITerm> metadata) throws FixedPointLimitException {
        // Phase 1: initialisation
        for (CFGNode n : cfg.nodes()) {
            setProperty(n, prop, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) metadata.lattice().bottom());
        }

        final BinaryRelation<CFGNode, CFGNode> edges;
        final Iterable<Set<CFGNode>> sccs;
        switch (metadata.dir()) {
            case Forward: {
                for (CFGNode n : cfg.startNodes()) {
                    setProperty(n, prop, new meta.flowspec.java.interpreter.values.Set<>());
                }
                edges = cfg.edges();
                sccs = cfg.topoSCCs();
                break;
            }
            case Backward: {
                for (CFGNode n : cfg.endNodes()) {
                    setProperty(n, prop, new meta.flowspec.java.interpreter.values.Set<>());
                }
                edges = cfg.edges().inverse();
                sccs = cfg.revTopoSCCs();
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }

        // Phase 2: Fixpoint iteration
        for(Set<CFGNode> scc : sccs) {
            boolean done = false;
            int fixpointCount = 0;
            while (!done) {
                if (fixpointCount >= FIXPOINT_LIMIT) {
                    throw new FixedPointLimitException(prop, FIXPOINT_LIMIT);
                }
                done = true;
                fixpointCount++;
                for (CFGNode from : scc) {
                    for (CFGNode to : edges.get(from)) {
                        ITerm afterFromTF = callTF(prop, metadata, from);
                        ITerm beforeToTF = getProperty(to, prop);
                        if (metadata.lattice().nlte(afterFromTF, beforeToTF)) {
                            setProperty(to, prop, metadata.lattice().lub(beforeToTF, afterFromTF));
                            if (scc.contains(to)) {
                                done = false;
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
                for (CFGNode n : cfg.nodes()) {
                    ITerm value = callTF(prop, metadata, n);
                    setPostProperty(n, prop, value);
                }
                break;
            }
            case Backward: {
                HashMap<CFGNode, ITerm> temp = new HashMap<>();
                for (CFGNode n : cfg.nodes()) {
                    setPostProperty(n, prop, getProperty(n, prop));
                    ITerm value = callTF(prop, metadata, n);
                    temp.put(n, value);
                }
                temp.forEach((n, value) -> {
                    setProperty(n, prop, value);
                });
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
    }

    private ITerm callTF(String prop, Metadata<?> metadata, CFGNode node) {
        TransferFunctionAppl tfAppl = solution.getTFAppl(node, prop);
        if (tfAppl == null) {
            return getProperty(node, prop);
        }
        return TransferFunction.call(tfAppl, metadata.transferFunctions(), node);
    }

    protected static class TimingInfo {
        private LinkedHashMap<String, Long> property;
        public final long start;
        private long interpInit;
        private long reverseTopo;
        private long end;
        
        public TimingInfo() {
            this.property = new LinkedHashMap<>();
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

            logger.info(message.toString());
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

    private ISolution flowspecCopyProperties(ISolution solution) {
            IFlowSpecSolution<CFGNode> flowspecSolution = solution.flowSpecSolution()
                    .withPreProperties(this.preProperties.freeze())
                    .withPostProperties(this.postProperties.freeze());

            return solution.withFlowSpecSolution(flowspecSolution);
        }
}
