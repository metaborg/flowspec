package mb.flowspec.controlflow;

import java.util.Map.Entry;
import java.util.Set;

public interface ICompleteControlFlowGraph extends IBasicControlFlowGraph {
    interface Immutable extends ICompleteControlFlowGraph, IBasicControlFlowGraph.Immutable {
        /**
         * @return A set of unreachable nodes in the control flow graph(s)
         */
        Set<ICFGNode> unreachableNodes();

        /**
         * @return An *unmodifiable* iterable of *unmodifiable* sets. The iterable is topologically ordered.
         * Each set is a strongly connected component (SCC) in the control flow graph(s) with a reverse
         * post-order over the depth-first spanning tree.
         * The ordering guarantees that if data is propagated along the out-edges of each node when visited in
         * order, you only need to initialise the data of the start nodes, every other node will have received
         * some data before being visited. 
         */
        Iterable<Set<ICFGNode>> topoSCCs();

        /**
         * @return An *unmodifiable* iterable of *unmodifiable* sets. The iterable is reverse topologically
         * ordered. Each set is a strongly connected component (SCC) in the control flow graph(s) with a reverse
         * post-order over the depth-first spanning tree of the inverse SCC graph.
         * The ordering guarantees that if data is propagated along the out-edges of each node when visited in
         * order, you only need to initialise the data of the end nodes, every other node will have received
         * some data before being visited.
         */
        Iterable<Set<ICFGNode>> revTopoSCCs();
    }

    interface Transient extends ICompleteControlFlowGraph, IBasicControlFlowGraph.Transient {
         default boolean addAll(ICompleteControlFlowGraph other) {
             boolean change = false;
             for (Entry<ICFGNode, ICFGNode> e : other.edges().entrySet()) {
                 change |= edges().__insert(e.getKey(), e.getValue());
             }
             change |= startNodes().addAll(other.startNodes());
             change |= normalNodes().addAll(other.normalNodes());
             change |= endNodes().addAll(other.endNodes());
             return change;
         }

        default ICompleteControlFlowGraph.Immutable freeze() {
            return CompleteControlFlowGraph.of(normalNodes(), edges().freeze(), startNodes(),
                    endNodes(), entryNodes(), exitNodes());
        }
    }
}
