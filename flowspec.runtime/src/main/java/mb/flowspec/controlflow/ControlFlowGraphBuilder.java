package mb.flowspec.controlflow;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import io.usethesource.capsule.BinaryRelation;
import mb.flowspec.controlflow.ICFGNode.Kind;

public class ControlFlowGraphBuilder {
    private final BinaryRelation.Transient<ICFGNode, ICFGNode> edges;
    private final Set<ICFGNode> startNodes;
    private final Set<ICFGNode> normalNodes;
    private final Set<ICFGNode> endNodes;
    private final Set<ICFGNode> entryNodes;
    private final Set<ICFGNode> exitNodes;

    private ControlFlowGraphBuilder(BinaryRelation.Transient<ICFGNode, ICFGNode> edges, Set<ICFGNode> startNodes,
        Set<ICFGNode> normalNodes, Set<ICFGNode> endNodes, Set<ICFGNode> entryNodes, Set<ICFGNode> exitNodes) {
        this.edges = edges;
        this.startNodes = startNodes;
        this.normalNodes = normalNodes;
        this.endNodes = endNodes;
        this.entryNodes = entryNodes;
        this.exitNodes = exitNodes;
    }

    /**
     * @return The value of the {@code edges} attribute
     */
    public BinaryRelation.Transient<ICFGNode, ICFGNode> edges() {
        return edges;
    }

    /**
     * @return The value of the {@code startNodes} attribute
     */
    public Set<ICFGNode> startNodes() {
        return startNodes;
    }

    /**
     * @return The value of the {@code normalNodes} attribute
     */
    public Set<ICFGNode> normalNodes() {
        return normalNodes;
    }

    /**
     * @return The value of the {@code endNodes} attribute
     */
    public Set<ICFGNode> endNodes() {
        return endNodes;
    }

    /**
     * @return The value of the {@code entryNodes} attribute
     */
    public Set<ICFGNode> entryNodes() {
        return entryNodes;
    }

    /**
     * @return The value of the {@code exitNodes} attribute
     */
    public Set<ICFGNode> exitNodes() {
        return exitNodes;
    }

    public static ControlFlowGraphBuilder of() {
        return new ControlFlowGraphBuilder(BinaryRelation.Transient.of(), new HashSet<>(), new HashSet<>(),
            new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    public IControlFlowGraph build() {
        final Set<ICFGNode> normalNodes = Collections.unmodifiableSet(normalNodes());
        final Set<ICFGNode> startNodes = Collections.unmodifiableSet(startNodes());
        final Set<ICFGNode> endNodes = Collections.unmodifiableSet(endNodes());
        final Set<ICFGNode> entryNodes = Collections.unmodifiableSet(entryNodes());
        final Set<ICFGNode> exitNodes = Collections.unmodifiableSet(exitNodes());
        final BinaryRelation.Immutable<ICFGNode, ICFGNode> edges = edges().freeze();

        final Set<IBasicBlock> startBlocks = new HashSet<>();
        final Set<IBasicBlock> endBlocks = new HashSet<>();
        final BinaryRelation.Transient<IBasicBlock, IBasicBlock> blockEdges = BinaryRelation.Transient.of();

        for(ICFGNode start : startNodes) {
            startBlocks.add(buildBlock(start, edges, blockEdges, endBlocks));
        }
        return ControlFlowGraph.of(edges, blockEdges.freeze(), Collections.unmodifiableSet(startBlocks),
            Collections.unmodifiableSet(endBlocks), startNodes, endNodes, entryNodes, exitNodes, normalNodes);
    }

    private static IBasicBlock buildBlock(ICFGNode node, BinaryRelation.Immutable<ICFGNode, ICFGNode> edges,
        BinaryRelation.Transient<IBasicBlock, IBasicBlock> blockEdges, Set<IBasicBlock> endBlocks) {
        final Deque<ICFGNode> block = new ArrayDeque<>();
        final IBasicBlock result = new BasicBlock(block);
        loop: while(true) {
            block.addLast(node);
            final Set<ICFGNode> nexts = edges.get(node);
            switch(nexts.size()) {
                case 0:
                    assert node.getKind() == Kind.End;
                    endBlocks.add(result);
                    break loop;
                case 1:
                    node = nexts.iterator().next();
                    break;
                default:
                    for(ICFGNode next : nexts) {
                        blockEdges.__insert(result, buildBlock(next, edges, blockEdges, endBlocks));
                    }
                    break loop;
            }
        }
        return result;
    }
}
