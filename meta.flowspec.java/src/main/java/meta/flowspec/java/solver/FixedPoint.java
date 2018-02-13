package meta.flowspec.java.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.ControlFlowGraph;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.controlflow.terms.TransferFunctionAppl;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.solver.ImmutableSolution;
import org.metaborg.meta.nabl2.stratego.ImmutableTermIndex;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.IStringTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.terms.generic.TB;
import org.metaborg.meta.nabl2.util.collections.IProperties;
import org.metaborg.meta.nabl2.util.tuples.Tuple2;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.java.interpreter.UnreachableException;

public abstract class FixedPoint {
    private static final ILogger logger = LoggerUtils.logger(FixedPoint.class);
    private static final String ARTIFICIAL_PROPERTY = "__START__";
    private static FixedPoint.TimingInfo timingInfo;
    // TODO: Make a config variable
    private static final int FIXPOINT_LIMIT = 10_000;

    public static ISolution entryPoint(ISolution nabl2solution, TFFileInfo tfFileInfo) {
        // FIXME: this is an evil workaround, do better API design for CFG
        final ControlFlowGraph<CFGNode> cfg = (ControlFlowGraph<CFGNode>) nabl2solution.controlFlowGraph();

        FixedPoint.flowspecCopyTFAppls(cfg, nabl2solution.astProperties());
        timingInfo = new FixedPoint.TimingInfo();

        /* Pass the NaBL2 solution to the interpreter AST so it can save references to the CFG and the
         * resolution result in certain places
         */
        tfFileInfo.init(nabl2solution);

        // remove artificial nodes from CFG & compute the SCCs
        cfg.complete();

        timingInfo.recordCfgProcessing();

        List<CFGNode> unreachable = cfg.getUnreachableNodes();
        if (!unreachable.isEmpty()) {
            logger.warn("Found unreachable CFG nodes: " + unreachable);
        }

        logger.debug("SCCs:" + cfg.getTopoSCCs());

        try {
            solve(cfg, tfFileInfo);

            timingInfo.recordEnd();

            // TODO: add config to turn on timing info
            if (true) {
                timingInfo.logReport(logger);
            }

            return FixedPoint.flowspecCopyProperties(nabl2solution);
        } catch (UnimplementedException | UnreachableException | ParseException | CyclicGraphException | FixedPointLimitException e) {
            logger.error(e.getMessage());

            return nabl2solution;
        }
    }

    public static void solve(ControlFlowGraph<CFGNode> cfg, TFFileInfo tfFileInfo)
            throws CyclicGraphException, FixedPointLimitException {
        BinaryRelation.Immutable<String, String> propDependsOn;
        { // Make sure every property is in the dependency graph's edges by adding an artificial edge.
          // This way the later topoSort of the edges will give all properties and you just need
          //  to remove the artificial start node. 
            BinaryRelation.Transient<String, String> propDep = tfFileInfo.dependsOn().asTransient();
            for (Entry<String, String> entry : tfFileInfo.dependsOn().entrySet()) {
                String prop = entry.getKey();
                propDep.__insert(ARTIFICIAL_PROPERTY, prop);
            }
            propDependsOn = propDep.freeze();
        }

        List<String> propTopoOrder = topoSort(propDependsOn);
        Collections.reverse(propTopoOrder);

        timingInfo.recordReverseTopo();

        for (String prop : propTopoOrder) {
            // remove artificial start used earlier to include all properties in the dependency graph
            if(prop != ARTIFICIAL_PROPERTY) {
                solveFlowSensitiveProperty(cfg, prop, tfFileInfo.metadata().get(prop));
                timingInfo.recordProperty(prop);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void solveFlowSensitiveProperty(ControlFlowGraph<CFGNode> cfg,
            String prop, Metadata metadata) throws FixedPointLimitException {
        // Phase 1: initialisation

        for (CFGNode n : cfg.getAllNodes()) {
            cfg.setProperty(n, prop, new meta.flowspec.java.interpreter.values.Set<>());
            /* No need to set a different value for the start node, since the transfer function for the
             * start node will (unconditionally) result in that value, which will be propagated Phase 2.
             */
        }

        // Phase 2: Fixpoint iteration
        final BinaryRelation<CFGNode, CFGNode> edges;
        switch (metadata.dir()) {
            case Forward: {
                edges = cfg.getDirectEdges();
                break;
            }
            case Backward: {
                edges = cfg.getDirectEdges().inverse();
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }

        Collection<Set<CFGNode>> nodes = cfg.getTopoSCCs();

        for(Set<CFGNode> scc : nodes) {
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
                        Object afterFromTF = TransferFunction.call(cfg.getTFAppl(from, prop), metadata.transferFunctions(), from);
                        Object beforeToTF = cfg.getProperty(to, prop);
                        if (metadata.lattice().nlte(afterFromTF, beforeToTF)) {
                            cfg.setProperty(to, prop, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) metadata.lattice().lub(beforeToTF, afterFromTF));
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
        final String prePropName;
        final String postPropName;
        switch (metadata.dir()) {
            case Forward: {
                 prePropName = "pre-" + prop;
                postPropName = prop;
                break;
            }
            case Backward: {
                prePropName = prop;
                postPropName = "pre-" + prop;
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
        for (CFGNode n : cfg.getAllNodes()) {
            // save pre-TF results
            cfg.setProperty(n, prePropName, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) cfg.getProperty(n, prop));
            // put post-TF results in property name
            cfg.setProperty(n, postPropName, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) TransferFunction.call(cfg.getTFAppl(n, prop), metadata.transferFunctions(), n));
        }
    }

    /**
     * Interprets the relation as an edge list for a graph and gives a
     * topological sorted list of its nodes. The used algorithm is due to Kahn
     * (1962):
     * https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param rel
     *            The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional
     *         if there are cycles in the graph
     */
    public static <E> List<E> topoSort(BinaryRelation.Immutable<E, E> rel) throws CyclicGraphException {
        List<E> result = new ArrayList<>();
        // The frontier is initialised with nodes that have no incoming edges.
        Set<E> frontier = new HashSet<>(rel.keySet());
        frontier.removeAll(rel.values());
        BinaryRelation.Transient<E, E> mutRel = rel.asTransient();

        // (1) Move the nodes from the frontier (no incoming edges) to the
        // result
        // (2) Remove outgoing edges of each node moved from frontier to result
        // (3) Add nodes that now no longer have incoming edges to frontier
        while (!frontier.isEmpty()) {
            E node = frontier.iterator().next();
            frontier.remove(node);
            result.add(node);
            for (E rhs : mutRel.get(node)) {
                mutRel.__remove(node, rhs);
                if (!mutRel.containsValue(rhs)) {
                    frontier.add(rhs);
                }
            }
        }
        // If graph is not empty when the frontier became empty, there must be a
        // cycle in the graph
        if (!mutRel.isEmpty()) {
            throw new CyclicGraphException(mutRel.freeze());
        }
        return result;
    }
    
    static class TimingInfo {
        private LinkedHashMap<String, Long> property;
        public final long start;
        private long cfgProcessing;
        private long reverseTopo;
        private long end;
        
        public TimingInfo() {
            this.property = new LinkedHashMap<>();
            this.start = System.nanoTime();
        }
        
        public void logReport(ILogger logger) {
            long total = millisecondBetween(this.start, this.end);
            long cfgProcessing = millisecondBetween(this.start, this.cfgProcessing);
            long reverseTopo = millisecondBetween(this.cfgProcessing, this.reverseTopo);
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
            message.append("|- CFG artificial node removal & SCC computation: " + cfgProcessing + "\n");
            message.append("|- Reverse topo order of properties: " + reverseTopo + "\n");
            message.append("|- Total dataflow property computation: " + properties + "\n");
            for (int i = 0; i < propertyNames.length; i++) {
                message.append("  |- Compute property '" + propertyNames[i] + "': " + propertyTimes[i] + "\n");
            }

            logger.info(message.toString());
        }

        public void recordCfgProcessing() {
            this.cfgProcessing = System.nanoTime();
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

    static void flowspecCopyTFAppls(ControlFlowGraph<CFGNode> cfg, IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties) {
        astProperties.stream().forEach(tuple -> {
            ITerm key = tuple._2();
            M.appl1("TF", M.string(), (keyAppl, propName) -> {
                return propName.getValue();
            }).match(key).ifPresent(propName -> {
                TermIndex index = tuple._1();
                ITerm tfApplTerm = tuple._3();
                TransferFunctionAppl.match().match(tfApplTerm).ifPresent(tfAppl -> {
                    cfg.addTFAppl(index, propName, tfAppl);
                });
            });
        });
    }

    static ISolution flowspecCopyProperties(ISolution solution) {
            logger.debug("Copying FlowSpec properties to NaBL2 ast properties in solution");
            IProperties.Transient<TermIndex, ITerm, ITerm> astProperties = solution.astProperties().melt();
            IControlFlowGraph<CFGNode> controlFlowGraph = solution.controlFlowGraph();

            for (Map.Entry<Tuple2<CFGNode, String>, ITerm> property : controlFlowGraph.getProperties().entrySet()) {
                CFGNode node = property.getKey()._1();
                String propName = property.getKey()._2();
                ITerm value = property.getValue();

                TermIndex ti = TermIndex.get(node).orElse(ImmutableTermIndex.of(node.getResource(), 0));

                astProperties.putValue(ti, TB.newAppl("DFProperty", TB.newString(propName)), value);
            }

            return ImmutableSolution.of(solution.config(), astProperties.freeze(), solution.scopeGraph(),
                    solution.nameResolution(), solution.declProperties(), solution.relations(), solution.unifier(),
                    solution.symbolic(), solution.controlFlowGraph(), solution.messages(), solution.constraints());
        }
}
