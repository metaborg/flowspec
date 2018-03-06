package mb.flowspec.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.BinaryRelation;
import mb.flowspec.java.solver.CyclicGraphException;
import mb.flowspec.graph.ImmutableTopoSCCResult;

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


    /**
     * The topologically sorted list of strongly connected components of the control-flow graph. In this
     * case we have start nodes for the graph so this is a slight adaptation where the nodes that are
     * unreachable from the start are returned as a separate list and are not analysed. We also sort the
     * SCCs internally to have a reverse postorder within the component. 
     * The basic algorithm is the SCC algorithm of Tarjan (1972), adapted so it doesn't give the _reverse_
     * topological sorted SCCs. 
     * @return A tuple of the topologically sorted list of strongly connected components and the unreachable nodes.
     */
    public static <N> TopoSCCResult<N> topoSCCs(Collection<N> nodes, Collection<N> startNodes, Collection<N> endNodes, BinaryRelation.Immutable<N, N> edges) {
        int index = 0;
        HashMap<N, Integer> nodeIndex = new HashMap<>(nodes.size());
        HashMap<N, Integer> nodeLowlink = new HashMap<>(nodeIndex);
        Deque<N> sccStack = new ArrayDeque<>();
        java.util.Set<N> stackSet = new HashSet<>();
        Deque<java.util.Set<N>> sccs = new ArrayDeque<>();
        ArrayList<N> unreachable = new ArrayList<>();

        /* Note these deviations: 
         * (1) We seed the traversal with the start nodes.
         * (2) We use a deque of SCCs, so be can push to the front of it. 
         */
        for (N node : startNodes) {
            // For each start node that hasn't been visited already,
            if (nodeIndex.get(node) == null) {
                // do the recursive strong-connect
                index = sccStrongConnect(edges, node, index, nodeIndex, nodeLowlink, sccStack, stackSet, sccs);
            }
        }

        for (N node : nodes) {
            // Every node not yet visited from the start nodes is unreachable
            if (nodeIndex.get(node) == null) {
                unreachable.add(node);
            }
        }

        // Now the inverse graph
        nodeIndex.clear();
        nodeLowlink.clear();
        assert sccStack.isEmpty();
        assert stackSet.isEmpty();
        Deque<java.util.Set<N>> revSCCs = new ArrayDeque<>();
        BinaryRelation.Immutable<N, N> inverseEdges = edges.inverse();
        index = 0;

        for (N node : endNodes) {
            // For each start node that hasn't been visited already,
            if (nodeIndex.get(node) == null) {
                // do the recursive strong-connect
                index = sccStrongConnect(inverseEdges, node, index, nodeIndex, nodeLowlink, sccStack, stackSet, revSCCs);
            }
        }

        return ImmutableTopoSCCResult.of(
                Collections.unmodifiableCollection(sccs),
                Collections.unmodifiableCollection(revSCCs),
                Collections.unmodifiableList(unreachable));
    }

    /**
     * Recursively (DFS) walk the graph and give nodes an index. The lowlink is the lowest index of a node
     * that it can reach *through the DFS*. Therefore once those numbers are propagated, you can find an SCC
     * by finding all nodes with the same lowlink value. Given the way the algorithm works, when on the way
     * back from the DFS you find a node which still has the same index and lowlink value, this can be
     * considered the root of an SCC, and all the nodes above it on the stack are also in that SCC. So you
     * can simply pop nodes of the stack that was kept while doing the DFS (without inspecting them), until
     * you find this node with the same values.
     * As an adaption we also order the nodes in the SCC. We add visited nodes to the stack in post-order
     * even though the set of things on the stack is kept in pre-order. This allows us to very easily give
     * the nodes within an SCC in reverse-postorder
     * @param from The node to start from
     * @param index The index to start from
     * @param nodeIndex The mapping from node to index
     * @param nodeLowlink The mapping from node to lowest index reachable from this node
     * @param sccStack A stack of nodes being visited during the DFS (*not* used _for_ the DFS, it's recursive not iterative)
     * @param stackSet The set of nodes on the stack for easier checking if something's on the stack
     * @param sccs The list of SCCs
     * @return The new index value
     */
    private static <N> int sccStrongConnect(BinaryRelation<N, N> edges, N from, int index, HashMap<N, Integer> nodeIndex, 
            HashMap<N, Integer> nodeLowlink, Deque<N> sccStack, java.util.Set<N> stackSet, Deque<java.util.Set<N>> sccs) {
        nodeIndex.put(from, index);
        nodeLowlink.put(from, index);
        index++;

        // Note that we don't actually add the node to the stack, we just say it's on there with this set
        int stackSetSizeBefore = stackSet.size();
        stackSet.add(from);

        for (N to : edges.get(from)) {
            if (nodeIndex.get(to) == null) {
                // Visit neighbours without an index. Propagate lowlink values backward. 
                index = sccStrongConnect(edges, to, index, nodeIndex, nodeLowlink, sccStack, stackSet, sccs);
                nodeLowlink.put(from, Integer.min(nodeLowlink.get(from), nodeLowlink.get(to)));
            } else if (stackSet.contains(to)) {
                /* Neighbours already in the stack are higher in the DFS spanning tree, so we use their
                 * index, not their lowlink. Using the lowlink doesn't break the algorithm, but doesn't help
                 * and makes the lowlink have a less predictable value which cannot be given a clear
                 * meaning.
                 */
                nodeLowlink.put(from, Integer.min(nodeLowlink.get(from), nodeIndex.get(to)));
            }
        }

        // Here we actually add the node to the stack, in _postorder_
        sccStack.add(from);

        if (nodeLowlink.get(from) == nodeIndex.get(from)) {
            // Pop the SCC of the stack; since it's a stack, we get a reverse postorder
            java.util.LinkedHashSet<N> scc = new LinkedHashSet<>();
            for(int i = stackSet.size(); i > stackSetSizeBefore; i--) {
                N node = sccStack.pop();
                stackSet.remove(node);
                scc.add(node);
            }
            // AddFirst so we get a topological ordering, not a reverse topological ordering
            sccs.addFirst(Collections.unmodifiableSet(scc));
        }

        return index;
    }

    @Immutable
    static interface TopoSCCResult<N> {
        @Parameter
        Iterable<java.util.Set<N>> topoSCCs();
        @Parameter
        Iterable<java.util.Set<N>> revTopoSCCs();
        @Parameter
        Iterable<N> unreachables();
    }
}
