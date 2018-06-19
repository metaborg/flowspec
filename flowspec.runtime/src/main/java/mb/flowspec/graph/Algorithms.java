package mb.flowspec.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import io.usethesource.capsule.BinaryRelation;
import mb.flowspec.runtime.solver.CyclicGraphException;

public class Algorithms {
    /**
     * Interprets the relation as an edge list for a graph and gives a
     * topological sorted list of its nodes. The used algorithm is due to Kahn
     * (1962):
     * https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param edges
     *            The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional
     *         if there are cycles in the graph
     */
    public static <N> Iterable<N> topoSort(Collection<N> nodes, BinaryRelation.Immutable<N, N> edges) throws CyclicGraphException {
        List<N> result = new ArrayList<>();
        // The frontier is initialised with nodes that have no incoming edges.
        java.util.Set<N> frontier = new HashSet<>(nodes);
        frontier.removeAll(edges.values());
        BinaryRelation.Transient<N, N> mutRel = edges.asTransient();
    
        // (1) Move the nodes from the frontier (no incoming edges) to the
        // result
        // (2) Remove outgoing edges of each node moved from frontier to result
        // (3) Add nodes that now no longer have incoming edges to frontier
        while (!frontier.isEmpty()) {
            N node = frontier.iterator().next();
            frontier.remove(node);
            result.add(node);
            for (N rhs : mutRel.get(node)) {
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
}
