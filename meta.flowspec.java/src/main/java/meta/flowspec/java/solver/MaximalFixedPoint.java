package meta.flowspec.java.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.BinaryRelation;
import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;
import meta.flowspec.nabl2.controlflow.impl.ControlFlowGraph;
import meta.flowspec.nabl2.util.collections.IRelation2;

public abstract class MaximalFixedPoint {
    public static <CFGNode extends ICFGNode> void solve(
            ControlFlowGraph<CFGNode> cfg,
            Map<String, Metadata> propMetadata,
            BinaryRelation.Immutable<String, String> propDependsOn,
            Map<String, TransferFunction[]> transferFuns) {
        if (propDependsOn.isEmpty() && propMetadata.size() == 1) {
            for (Entry<String, Metadata> entry : propMetadata.entrySet()) {
                solveProperty(cfg, entry.getKey(), entry.getValue(), transferFuns);
            }
        } else {
            // TODO: statically check for cycles in property dependencies in FlowSpec
            List<String> propTopoOrder = topoSort(propDependsOn).get();
            Collections.reverse(propTopoOrder);
    
            for (String prop : propTopoOrder) {
                solveProperty(cfg, prop, propMetadata.get(prop), transferFuns);
            }
        }
    }
    
    private static <CFGNode extends ICFGNode> void solveProperty(
            ControlFlowGraph<CFGNode> cfg, 
            String prop, 
            Metadata metadata,
            Map<String, TransferFunction[]> transferFuns) {
        if (metadata.dir() == Metadata.Direction.FlowInsensitive) {
            solveFlowInsensitiveProperty(cfg, prop);
        } else {
            solveFlowSensitiveProperty(cfg, prop, metadata, transferFuns);
        }
    }
    
    private static <CFGNode extends ICFGNode> void solveFlowSensitiveProperty(
            ControlFlowGraph<CFGNode> cfg, 
            String prop, 
            Metadata metadata,
            Map<String, TransferFunction[]> transferFuns) {
        // Phase 1: initialisation
        TransferFunction[] tf = transferFuns.get(prop);

        for (CFGNode n : cfg.getAllCFGNodes()) {
            cfg.setProperty(n, prop, metadata.lattice().bottom());
            // No need to set a different value for the start node, since the rule for the start node will result
            //  in that value, which will be propagated Phase 2. 
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
            default: {
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
            }
        }

        // TODO: start at start node (or end node in case of Backward dir)
        java.util.Set<CFGNode> workList = new java.util.HashSet<>(edges.keySet());

        while (!workList.isEmpty()) {
            final CFGNode from = workList.iterator().next();
            workList.remove(from);
            for (CFGNode to : edges.get(from)) {
                Object afterFromTF = cfg.getTFAppl(from, prop).call(tf, from);
                Object beforeToTF = cfg.getProperty(to, prop);
                // TODO: use nlte instead of !lte
                if (!metadata.lattice().lte(afterFromTF, beforeToTF)) {
                    cfg.setProperty(to, prop, metadata.lattice().lub(beforeToTF, afterFromTF));
                    workList.add(to);
                }
            }
        }

        // Phase 3: Result calculation
        for (CFGNode n : cfg.getAllCFGNodes()) {
            // save pre-TF results
            cfg.setProperty(n, "Pre-" + prop, cfg.getProperty(n, prop));
            // put post-TF results in property name
            cfg.setProperty(n, prop, cfg.getTFAppl(n, prop).call(tf, n));
        }
        
    }

    private static <CFGNode extends ICFGNode> void solveFlowInsensitiveProperty(IControlFlowGraph<CFGNode> cfg, String prop) {
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Interprets the relation as an edge list for a graph and gives a topological sorted list of its nodes.
     * The used algorithm is due to Kahn (1962): https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param rel The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional if there are cycles in the graph
     */
    public static <E> Optional<List<E>> topoSort(BinaryRelation.Immutable<E, E> rel) {
        List<E> result = new ArrayList<>();
        // The frontier is initialised with nodes that have no incoming edges.
        Set<E> frontier = new HashSet<>(rel.keySet());
        frontier.removeAll(rel.values());
        BinaryRelation.Transient<E, E> mutRel = rel.asTransient();
   
        // (1) Move the nodes from the frontier (no incoming edges) to the result
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
        // If graph is not empty when the frontier became empty, there must be a cycle in the graph
        if (!mutRel.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result);
    }
}
