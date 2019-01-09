package mb.flowspec.controlflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import io.usethesource.capsule.BinaryRelation;
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
        @Override BinaryRelation.Immutable<ICFGNode, ICFGNode> edges();

        default Map<TermIndex, ICFGNode> startNodeMap() {
            Map<TermIndex, ICFGNode> map = new HashMap<>(startNodes().size());
            startNodes().stream().forEach(node -> {
                map.put(node.getIndex(), node);
            });
            return map;
        }

        default Map<TermIndex, ICFGNode> endNodeMap() {
            Map<TermIndex, ICFGNode> map = new HashMap<>(endNodes().size());
            endNodes().stream().forEach(node -> {
                map.put(node.getIndex(), node);
            });
            return map;
        }

        default Map<TermIndex, ICFGNode> entryNodeMap() {
            Map<TermIndex, ICFGNode> map = new HashMap<>(entryNodes().size());
            entryNodes().stream().forEach(node -> {
                map.put(node.getIndex(), node);
            });
            return map;
        }

        default Map<TermIndex, ICFGNode> exitNodeMap() {
            Map<TermIndex, ICFGNode> map = new HashMap<>(exitNodes().size());
            exitNodes().stream().forEach(node -> {
                map.put(node.getIndex(), node);
            });
            return map;
        }

        default Map<TermIndex, ICFGNode> normalNodeMap() {
            Map<TermIndex, ICFGNode> map = new HashMap<>(normalNodes().size());
            normalNodes().stream().forEach(node -> {
                map.put(node.getIndex(), node);
            });
            return map;
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
                    return Optional.empty();
            }
            return Optional.ofNullable(map.get(index));
        }
    }

    interface Transient extends IBasicControlFlowGraph {
        @Override BinaryRelation.Transient<ICFGNode, ICFGNode> edges();
    }
}