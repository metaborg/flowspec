package mb.flowspec.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;

import io.usethesource.capsule.BinaryRelation;

public class Algorithms {
    /**
     * Interprets the relation as an edge list for a graph and gives a topological sorted list of its nodes. The used
     * algorithm is due to Kahn (1962): https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param nodes
     *            The nodes of the graph (may contain unconnected nodes)
     * @param edges
     *            The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional if there are cycles in the graph
     */
    @SuppressWarnings("unchecked") public static <V> Iterable<V> topoSort(Collection<V> nodes,
        BinaryRelation.Immutable<V, V> edges) {
        HashSet<V> nodeSet = new HashSet<>(nodes);
        return topoSort((HashSet<V>) nodeSet.clone(), edges, nodeSet);
    }

    /**
     * Interprets the relation as an edge list for a graph and gives a topological sorted list of its nodes. The used
     * algorithm is due to Kahn (1962): https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param nodes
     *            The nodes of the graph (may contain unconnected nodes)
     * @param edges
     *            The relation / graph
     * @param subgraph
     *            The subgraph to consider
     * @return A list of "vertices" in topological order, or an empty optional if there are cycles in the graph
     */
    private static <V> Iterable<V> topoSort(Set<V> frontier, BinaryRelation.Immutable<V, V> edges, Set<V> subgraph) {
        List<V> result = new ArrayList<>(subgraph.size());
        // The frontier is initialised with nodes that have no incoming edges.
        frontier.removeAll(edges.values());
        frontier.retainAll(subgraph);
        BinaryRelation.Transient<V, V> mutRel = edges.asTransient();

        // (1) Move the nodes from the frontier (no incoming edges) to the
        // result
        // (2) Remove outgoing edges of each node moved from frontier to result
        // (3) Add nodes that now no longer have incoming edges to frontier
        while(!frontier.isEmpty()) {
            V node = frontier.iterator().next();
            frontier.remove(node);
            result.add(node);
            for(V rhs : mutRel.get(node)) {
                mutRel.__remove(node, rhs);
                if(!mutRel.containsValue(rhs) && !subgraph.contains(rhs)) {
                    frontier.add(rhs);
                }
            }
        }
        return result;
    }

    /**
     * Interprets the relation as an edge list for a graph and gives a reverse topological sorted list of its nodes. The
     * used algorithm is due to Kahn (1962): https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param edges
     *            The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional if there are cycles in the graph
     */
    public static <V> Iterable<V> topoDeps(Collection<V> startNodes, BinaryRelation.Immutable<V, V> edges) {
        Set<V> subgraph = new HashSet<>();
        Set<V> toVisit = new HashSet<>(startNodes);
        Set<V> topoStartNodes = new HashSet<>();

        while(!toVisit.isEmpty()) {
            Set<V> visiting = toVisit;
            toVisit = new HashSet<>();
            for(V node : visiting) {
                subgraph.add(node);
                Set<V> dependents = edges.get(node);
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

    /**
     * The topologically sorted list of strongly connected components of the control-flow graph. In this case we have
     * start nodes for the graph so this is a slight adaptation where the nodes that are unreachable from the start are
     * returned as a separate list and are not analysed. We also sort the SCCs internally to have a reverse postorder
     * within the component. The basic algorithm is the SCC algorithm of Tarjan (1972), adapted so it doesn't give the
     * _reverse_ topological sorted SCCs.
     * 
     * @return The topologically sorted list of strongly connected components.
     */
    public static <N> Collection<Set<N>> topoSCCs(Collection<N> startNodes, Function<N, ? extends Set<N>> next) {
        int index = 0;
        final HashMap<N, Integer> nodeIndex = new HashMap<>();
        final HashMap<N, Integer> nodeLowlink = new HashMap<>(nodeIndex);
        final Deque<N> sccStack = new ArrayDeque<>();
        final Set<N> stackSet = new HashSet<>();
        Deque<Set<N>> sccs = new ArrayDeque<>();

        /*
         * Note these deviations: (1) We seed the traversal with the start nodes. (2) We use a deque of SCCs, so be can
         * push to the front of it.
         */
        for(N node : startNodes) {
            // For each start node that hasn't been visited already,
            if(nodeIndex.get(node) == null) {
                // do the recursive strong-connect
                index = sccStrongConnect(next, node, index, nodeIndex, nodeLowlink, sccStack, stackSet, sccs);
            }
        }

        return Collections.unmodifiableCollection(sccs);
    }

    /**
     * The reverse topologically sorted list of strongly connected components of the control-flow graph. This is not
     * just a reversal of the list of strongly connected components but also a recomputation of the order with the SCC,
     * using a suitable depth-first spanning tree.
     * 
     * @param sccs
     *            The topologically sorted list of strongly connected components, where the SCCs have an iteration order
     *            in reverse postorder on the graph from the logical start node of the SCC.
     * @return The reverse topologically sorted list of strongly connected components
     */
    public static <N> Collection<Set<N>> revTopoSCCs(Function<N, ? extends Set<N>> prev, Collection<Set<N>> sccs) {
        // Now the inverse graph
        Deque<Set<N>> revSCCs = new ArrayDeque<>(sccs.size());
        /*
         * Reverse the order of the SCCs, and recompute the reverse postorder on the inverse graph for SCCs with
         * multiple nodes.
         */
        for(PeekingIterator<Set<N>> iterator = Iterators.peekingIterator(sccs.iterator()); iterator.hasNext();) {
            Set<N> scc = iterator.next();
            if(scc.size() != 1) {
                /*
                 * Find reasonable start node in SCC by picking one that has an edge to the next SCC's first node.
                 */
                assert iterator.hasNext();
                final N nextSCCsFirstNode = iterator.peek().iterator().next();
                final Set<N> hasEdgeToNextSCCsFirstNode = prev.apply(nextSCCsFirstNode);
                N node = Sets.intersection(hasEdgeToNextSCCsFirstNode, scc).iterator().next();
                // Recompute reverse postorder for inverse graph
                final Set<N> unvisited = new HashSet<>(scc);
                final Deque<N> visitingStack = new ArrayDeque<>();
                final Deque<N> revSCC = new ArrayDeque<>();
                Set<N> befores;
                visitingStack.addLast(node);
                do {
                    node = visitingStack.getLast();
                    unvisited.remove(node);
                    befores = Sets.intersection(prev.apply(node), unvisited);
                    if(!befores.isEmpty()) {
                        // visiting all the children at once so we don't need _another_ list of things
                        visitingStack.addAll(befores);
                        /*
                         * NOTE: Sets.intersection is a view that changes when the backing sets change, so we need a
                         * copy when we change unvisited!
                         */
                        unvisited.removeAll(Arrays.asList(befores.toArray()));
                    } else {
                        visitingStack.removeLast();
                        revSCC.addFirst(node);
                    }
                } while(!visitingStack.isEmpty());
                scc = Collections.unmodifiableSet(new LinkedHashSet<>(revSCC));
            }
            // addFirst for reverse order of SCCs
            revSCCs.addFirst(scc);
        }

        return Collections.unmodifiableCollection(revSCCs);
    }

    /**
     * Recursively (DFS) walk the graph and give nodes an index. The lowlink is the lowest index of a node that it can
     * reach *through the DFS*. Therefore once those numbers are propagated, you can find an SCC by finding all nodes
     * with the same lowlink value. Given the way the algorithm works, when on the way back from the DFS you find a node
     * which still has the same index and lowlink value, this can be considered the root of an SCC, and all the nodes
     * above it on the stack are also in that SCC. So you can simply pop nodes of the stack that was kept while doing
     * the DFS (without inspecting them), until you find this node with the same values. As an adaption we also order
     * the nodes in the SCC. We add visited nodes to the stack in post-order even though the set of things on the stack
     * is kept in pre-order. This allows us to very easily give the nodes within an SCC in reverse-postorder
     * 
     * @param from
     *            The node to start from
     * @param index
     *            The index to start from
     * @param nodeIndex
     *            The mapping from node to index
     * @param nodeLowlink
     *            The mapping from node to lowest index reachable from this node
     * @param sccStack
     *            A stack of nodes being visited during the DFS (*not* used _for_ the DFS, it's recursive not iterative)
     * @param stackSet
     *            The set of nodes on the stack for easier checking if something's on the stack
     * @param sccs
     *            The list of SCCs
     * @return The new index value
     */
    private static <N> int sccStrongConnect(Function<N, ? extends Set<N>> next, N from, int index,
        HashMap<N, Integer> nodeIndex, HashMap<N, Integer> nodeLowlink, Deque<N> sccStack, Set<N> stackSet,
        Deque<Set<N>> sccs) {
        nodeIndex.put(from, index);
        nodeLowlink.put(from, index);
        index++;

        // Note that we don't actually add the node to the stack, we just say it's on there with this set
        int stackSetSizeBefore = stackSet.size();
        stackSet.add(from);

        for(N to : next.apply(from)) {
            if(nodeIndex.get(to) == null) {
                // Visit neighbours without an index. Propagate lowlink values backward.
                index = sccStrongConnect(next, to, index, nodeIndex, nodeLowlink, sccStack, stackSet, sccs);
                nodeLowlink.put(from, Integer.min(nodeLowlink.get(from), nodeLowlink.get(to)));
            } else if(stackSet.contains(to)) {
                /*
                 * Neighbours already in the stack are higher in the DFS spanning tree, so we use their index, not their
                 * lowlink. Using the lowlink doesn't break the algorithm, but doesn't help and makes the lowlink have a
                 * less predictable value which cannot be given a clear meaning.
                 */
                nodeLowlink.put(from, Integer.min(nodeLowlink.get(from), nodeIndex.get(to)));
            }
        }

        // Here we actually add the node to the stack, in _postorder_
        sccStack.push(from);

        if(Objects.equals(nodeLowlink.get(from), nodeIndex.get(from))) {
            // Pop the SCC of the stack; since it's a stack, we get a reverse postorder
            java.util.LinkedHashSet<N> scc = new LinkedHashSet<>(2 * (stackSet.size() - stackSetSizeBefore));
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
}
