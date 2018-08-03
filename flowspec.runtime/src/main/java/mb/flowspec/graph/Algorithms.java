package mb.flowspec.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Set.Immutable;

public class Algorithms {
    /**
     * Interprets the relation as an edge list for a graph and gives a
     * topological sorted list of its nodes. The used algorithm is due to Kahn
     * (1962):
     * https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param nodes
     *            The nodes of the graph (may contain unconnected nodes)
     * @param edges
     *            The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional
     *         if there are cycles in the graph
     */
    @SuppressWarnings("unchecked")
    public static <V> Iterable<V> topoSort(Collection<V> nodes, BinaryRelation.Immutable<V, V> edges) {
        HashSet<V> nodeSet = new HashSet<>(nodes);
        return topoSort((HashSet<V>) nodeSet.clone(), edges, nodeSet);
    }

    /**
     * Interprets the relation as an edge list for a graph and gives a
     * topological sorted list of its nodes. The used algorithm is due to Kahn
     * (1962):
     * https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param nodes
     *            The nodes of the graph (may contain unconnected nodes)
     * @param edges
     *            The relation / graph
     * @param subgraph
     *            The subgraph to consider
     * @return A list of "vertices" in topological order, or an empty optional
     *         if there are cycles in the graph
     */
    private static <V> Iterable<V> topoSort(java.util.Set<V> frontier, BinaryRelation.Immutable<V, V> edges, java.util.Set<V> subgraph) {
        List<V> result = new ArrayList<>();
        // The frontier is initialised with nodes that have no incoming edges.
        frontier.removeAll(edges.values());
        frontier.removeIf(v -> !subgraph.contains(v));
        BinaryRelation.Transient<V, V> mutRel = edges.asTransient();

        // (1) Move the nodes from the frontier (no incoming edges) to the
        // result
        // (2) Remove outgoing edges of each node moved from frontier to result
        // (3) Add nodes that now no longer have incoming edges to frontier
        while (!frontier.isEmpty()) {
            V node = frontier.iterator().next();
            frontier.remove(node);
            result.add(node);
            for (V rhs : mutRel.get(node)) {
                mutRel.__remove(node, rhs);
                if (!mutRel.containsValue(rhs) && !subgraph.contains(rhs)) {
                    frontier.add(rhs);
                }
            }
        }
        return result;
    }

    /**
     * Interprets the relation as an edge list for a graph and gives a
     * reverse topological sorted list of its nodes. The used algorithm is due to Kahn
     * (1962):
     * https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param edges
     *            The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional
     *         if there are cycles in the graph
     */
    public static <V> Iterable<V> topoDeps(Collection<V> startNodes, BinaryRelation.Immutable<V, V> edges) {
        java.util.Set<V> subgraph = new HashSet<>();
        java.util.Set<V> toVisit = new HashSet<>(startNodes);
        java.util.Set<V> topoStartNodes = new HashSet<>();

        while(!toVisit.isEmpty()) {
            java.util.Set<V> visiting = toVisit;
            toVisit = new HashSet<>();
            for(V node : visiting) {
                subgraph.add(node);
                Immutable<V> dependents = edges.get(node);
                if(dependents.isEmpty()) {
                    topoStartNodes.add(node);
                } else {
                    for(V dependent : dependents) {
                        if(!subgraph.contains(dependent)) {
                            toVisit.add(dependent);
                        }
                    }
                }
            }
        }
        return topoSort(topoStartNodes, edges.inverse(), subgraph);
    }
}
