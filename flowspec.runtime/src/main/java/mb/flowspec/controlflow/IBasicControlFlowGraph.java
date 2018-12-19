package mb.flowspec.controlflow;

import java.util.Optional;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.flowspec.terms.TermIndex;

public interface IBasicControlFlowGraph {
    static final ILogger logger = LoggerUtils.logger(IBasicControlFlowGraph.class);

    /**
     * @return true if the graph is empty; i.e. has no nodes. 
     */
    default boolean isEmpty() {
        return nodes().isEmpty();
    }

    /**
     * @return All nodes in the control flow graph(s). This may take some computation.
     */
    Set<ICFGNode> nodes();

    /**
     * @return The edges of the control flow graph(s).
     */
    BinaryRelation<ICFGNode, ICFGNode> edges();

    /**
     * @return The start nodes of the control flow graph(s).
     */
    Set<ICFGNode> startNodes();

    /**
     * @return The end nodes of the control flow graph(s).
     */
    Set<ICFGNode> endNodes();

    /**
     * @return The entry nodes of the control flow graph(s).
     */
    Set<ICFGNode> entryNodes();

    /**
     * @return The exit nodes of the control flow graph(s).
     */
    Set<ICFGNode> exitNodes();

    /**
     * @return All nodes that are not start or end nodes
     */
    Set<ICFGNode> normalNodes();

    interface Immutable extends IBasicControlFlowGraph {
        @Override Set.Immutable<ICFGNode> nodes();
        @Override BinaryRelation.Immutable<ICFGNode, ICFGNode> edges();
        @Override Set.Immutable<ICFGNode> startNodes();
        @Override Set.Immutable<ICFGNode> normalNodes();
        @Override Set.Immutable<ICFGNode> endNodes();
        @Override Set.Immutable<ICFGNode> entryNodes();
        @Override Set.Immutable<ICFGNode> exitNodes();
        
        default Map.Immutable<TermIndex, ICFGNode> startNodeMap() {
            Map.Transient<TermIndex, ICFGNode> map = Map.Transient.of();
            startNodes().stream().forEach(node -> {
                map.__put(node.getIndex(), node);
            });
            return map.freeze();
        }
        
        default Map.Immutable<TermIndex, ICFGNode> endNodeMap() {
            Map.Transient<TermIndex, ICFGNode> map = Map.Transient.of();
            endNodes().stream().forEach(node -> {
                map.__put(node.getIndex(), node);
            });
            return map.freeze();
        }
        
        default Map.Immutable<TermIndex, ICFGNode> entryNodeMap() {
            Map.Transient<TermIndex, ICFGNode> map = Map.Transient.of();
            entryNodes().stream().forEach(node -> {
                map.__put(node.getIndex(), node);
            });
            return map.freeze();
        }
        
        default Map.Immutable<TermIndex, ICFGNode> exitNodeMap() {
            Map.Transient<TermIndex, ICFGNode> map = Map.Transient.of();
            exitNodes().stream().forEach(node -> {
                map.__put(node.getIndex(), node);
            });
            return map.freeze();
        }
        
        default Map.Immutable<TermIndex, ICFGNode> normalNodeMap() {
            Map.Transient<TermIndex, ICFGNode> map = Map.Transient.of();
            normalNodes().stream().forEach(node -> {
                map.__put(node.getIndex(), node);
            });
            return map.freeze();
        }

        /**
         * @return Find the CFG node associated with the following TermIndex, of the right kind
         */
        default Optional<ICFGNode> findNode(TermIndex index, ICFGNode.Kind kind) {
            final Map<TermIndex, ICFGNode> map;
            switch(kind) {
                case Normal:
                    map = normalNodeMap();
                    break;
                case Start:
                    map = startNodeMap();
                    break;
                case End:
                    map = endNodeMap();
                    break;
                case Entry:
                    map = entryNodeMap();
                    break;
                case Exit:
                    map = exitNodeMap();
                    break;
                default:
                    map = Map.Immutable.of();
                    break;
            }
            return Optional.ofNullable(map.get(index));
        }
    }

    interface Transient extends IBasicControlFlowGraph {
        @Override BinaryRelation.Transient<ICFGNode, ICFGNode> edges();
        @Override Set.Transient<ICFGNode> startNodes();
        @Override Set.Transient<ICFGNode> normalNodes();
        @Override Set.Transient<ICFGNode> endNodes();
        @Override Set.Transient<ICFGNode> entryNodes();
        @Override Set.Transient<ICFGNode> exitNodes();
    }
}