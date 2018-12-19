package mb.flowspec.controlflow;

import java.util.Map.Entry;

import io.usethesource.capsule.Set;

public interface ICompleteControlFlowGraph extends IBasicControlFlowGraph {
    interface Immutable extends ICompleteControlFlowGraph, IBasicControlFlowGraph.Immutable {
        /**
         * @return A set of unreachable nodes in the control flow graph(s)
         */
        Set.Immutable<ICFGNode> unreachableNodes();

        /**
         * @return A set of nodes that reach a dead end (not ending in an end node) in the control flow graph(s)
         */
        Set.Immutable<ICFGNode> deadEndNodes();

        /**
         * @return An *unmodifiable* iterable of *unmodifiable* sets. The iterable is topologically ordered.
         * Each set is a strongly connected component (SCC) in the control flow graph(s) with a reverse
         * post-order over the depth-first spanning tree.
         * The ordering guarantees that if data is propagated along the out-edges of each node when visited in
         * order, you only need to initialise the data of the start nodes, every other node will have received
         * some data before being visited. 
         */
        Iterable<java.util.Set<ICFGNode>> topoSCCs();

        /**
         * @return An *unmodifiable* iterable of *unmodifiable* sets. The iterable is reverse topologically
         * ordered. Each set is a strongly connected component (SCC) in the control flow graph(s) with a reverse
         * post-order over the depth-first spanning tree of the inverse SCC graph.
         * The ordering guarantees that if data is propagated along the out-edges of each node when visited in
         * order, you only need to initialise the data of the end nodes, every other node will have received
         * some data before being visited.
         */
        Iterable<java.util.Set<ICFGNode>> revTopoSCCs();
    }

    interface Transient extends ICompleteControlFlowGraph, IBasicControlFlowGraph.Transient {
         default boolean addAll(ICompleteControlFlowGraph other) {
             boolean change = false;
             for (Entry<ICFGNode, ICFGNode> e : other.edges().entrySet()) {
                 change |= edges().__insert(e.getKey(), e.getValue());
             }
             change |= startNodes().__insertAll(other.startNodes());
             change |= normalNodes().__insertAll(other.normalNodes());
             change |= endNodes().__insertAll(other.endNodes());
             return change;
         }

        default ICompleteControlFlowGraph.Immutable freeze() {
            return CompleteControlFlowGraph.of(normalNodes().freeze(), edges().freeze(), startNodes().freeze(),
                    endNodes().freeze(), entryNodes().freeze(), exitNodes().freeze());
        }
    }
}
