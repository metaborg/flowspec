package mb.flowspec.controlflow;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    private static IBasicBlock buildBlock(ICFGNode startNode, BinaryRelation.Immutable<ICFGNode, ICFGNode> edges,
        BinaryRelation.Transient<IBasicBlock, IBasicBlock> blockEdges, Set<IBasicBlock> endBlocks) {
        final Map<ICFGNode, IBasicBlock> builtBlocks = new HashMap<>();
        final Deque<BlockBuilder> workList = new ArrayDeque<>();
        final BlockBuilder startState = new BlockBuilder(startNode);
        workList.push(startState);

        call: while(!workList.isEmpty()) {
            final BlockBuilder bbs = workList.pop();

            block: while(true) {
                final Set<ICFGNode> nexts = edges.get(bbs.lastNode());
                switch(nexts.size()) {
                    case 0: { // final node: current block ends here
                        assert bbs.lastNode().getKind() == Kind.End;
                        endBlocks.add(bbs.block);
                        continue call;
                    }
                    case 1: { // straight ahead
                        final ICFGNode next = nexts.iterator().next();
                        if(edges.inverse().get(next).size() == 1) { // linear control flow: current block continues
                            bbs.addNode(next);
                            continue block;
                        } else { // merge point: current block ends, next node is start of next block
                            nextBlock(blockEdges, builtBlocks, workList, bbs, next);
                            continue call;
                        }
                    }
                    default: { // split point: current block ends, nodes in nexts each start next blocks
                        for(ICFGNode next : nexts) {
                            nextBlock(blockEdges, builtBlocks, workList, bbs, next);
                        }
                        continue call;
                    }
                }
            }
        }

        return startState.block;
    }

    public static void nextBlock(BinaryRelation.Transient<IBasicBlock, IBasicBlock> blockEdges,
        final Map<ICFGNode, IBasicBlock> mergePoints, final Deque<BlockBuilder> workList, final BlockBuilder bbs,
        final ICFGNode next) {
        IBasicBlock nextBlock = mergePoints.get(next); // check if already handled
        if(nextBlock == null) { // if not seen before, start a new block
            final BlockBuilder nextBbs = new BlockBuilder(next);
            workList.push(nextBbs);
            nextBlock = nextBbs.block;
            mergePoints.put(next, nextBlock);
        }
        blockEdges.__insert(bbs.block, nextBlock);
    }

    private static final class BlockBuilder {
        private final Deque<ICFGNode> blockDeque;
        public final IBasicBlock block;

        public BlockBuilder(ICFGNode node) {
            this.blockDeque = new ArrayDeque<>();
            this.block = new BasicBlock(blockDeque);
            addNode(node);
        }

        public ICFGNode lastNode() {
            return this.blockDeque.getLast();
        }

        public void addNode(ICFGNode node) {
            this.blockDeque.addLast(node);
        }
    }
}
