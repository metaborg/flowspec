package mb.flowspec.controlflow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import io.usethesource.capsule.BinaryRelation;
import mb.flowspec.graph.Algorithms;

public class ControlFlowGraph implements IControlFlowGraph, Serializable {
    private final BinaryRelation.Immutable<IBasicBlock, IBasicBlock> blockEdges;
    private final Set<IBasicBlock> startBlocks;
    private final Set<IBasicBlock> endBlocks;
    private final BinaryRelation.Immutable<ICFGNode, ICFGNode> edges;
    private final Set<ICFGNode> startNodes;
    private final Set<ICFGNode> endNodes;
    private final Set<ICFGNode> entryNodes;
    private final Set<ICFGNode> exitNodes;
    private final Set<ICFGNode> normalNodes;
    private transient Collection<Set<IBasicBlock>> topoSCCs;
    private transient Collection<Set<IBasicBlock>> revTopoSCCs;
    private final int nodeCount;

    private ControlFlowGraph(BinaryRelation.Immutable<ICFGNode, ICFGNode> edges,
        BinaryRelation.Immutable<IBasicBlock, IBasicBlock> blockEdges, Set<IBasicBlock> startBlocks,
        Set<IBasicBlock> endBlocks, Set<ICFGNode> startNodes, Set<ICFGNode> endNodes, Set<ICFGNode> entryNodes,
        Set<ICFGNode> exitNodes, Set<ICFGNode> normalNodes) {
        this.edges = edges;
        this.blockEdges = blockEdges;
        this.startBlocks = startBlocks;
        this.endBlocks = endBlocks;
        this.startNodes = startNodes;
        this.endNodes = endNodes;
        this.entryNodes = entryNodes;
        this.exitNodes = exitNodes;
        this.normalNodes = normalNodes;
        this.nodeCount = computeNodeCount();
    }

    /**
     * @return The value of the {@code edges} attribute
     */
    public BinaryRelation.Immutable<IBasicBlock, IBasicBlock> blockEdges() {
        return blockEdges;
    }

    /**
     * @return The value of the {@code startNodes} attribute
     */
    @Override public Set<IBasicBlock> startBlocks() {
        return startBlocks;
    }

    /**
     * @return The value of the {@code endNodes} attribute
     */
    @Override public Set<IBasicBlock> endBlocks() {
        return endBlocks;
    }

    /**
     * @return The value of the {@code edges} attribute
     */
    public BinaryRelation.Immutable<ICFGNode, ICFGNode> edges() {
        return edges;
    }

    @Override public Set<ICFGNode> nextNodes(ICFGNode from) {
        return edges.get(from);
    }

    @Override public Set<ICFGNode> prevNodes(ICFGNode from) {
        return edges.inverse().get(from);
    }

    @Override public Set<IBasicBlock> nextBlocks(IBasicBlock from) {
        return blockEdges().get(from);
    }

    @Override public Set<IBasicBlock> prevBlocks(IBasicBlock from) {
        return blockEdges().inverse().get(from);
    }

    /**
     * @return The value of the {@code startNodes} attribute
     */
    @Override public Set<ICFGNode> startNodes() {
        return startNodes;
    }

    /**
     * @return The value of the {@code endNodes} attribute
     */
    @Override public Set<ICFGNode> endNodes() {
        return endNodes;
    }

    /**
     * @return The value of the {@code entryNodes} attribute
     */
    @Override public Set<ICFGNode> entryNodes() {
        return entryNodes;
    }

    /**
     * @return The value of the {@code exitNodes} attribute
     */
    @Override public Set<ICFGNode> exitNodes() {
        return exitNodes;
    }

    /**
     * @return The value of the {@code normalNodes} attribute
     */
    @Override public Set<ICFGNode> normalNodes() {
        return normalNodes;
    }

    /**
     * @return The value of the {@code topoSCCs} attribute
     */
    @Override public Collection<Set<IBasicBlock>> topoSCCs() {
        if(topoSCCs == null) {
            topoSCCs = Algorithms.topoSCCs(startBlocks, blockEdges::get);
        }
        return topoSCCs;
    }

    /**
     * @return The value of the {@code revTopoSCCs} attribute
     */
    @Override public Collection<Set<IBasicBlock>> revTopoSCCs() {
        if(revTopoSCCs == null) {
            revTopoSCCs = Algorithms.revTopoSCCs(blockEdges.inverse()::get, topoSCCs());
        }
        return revTopoSCCs;
    }

    @Override public int nodeCount() {
        return nodeCount;
    }

    private int computeNodeCount() {
        return startNodes().size() + endNodes().size() + normalNodes().size() + entryNodes().size() + exitNodes().size();
    }

    @Override public int edgeCount() {
        return edges().size();
    }

    @Override public int blockCount() {
        return blockEdges().sizeDistinct();
    }

    @Override public int blockEdgeCount() {
        return blockEdges().size();
    }

    public static IControlFlowGraph of(BinaryRelation.Immutable<ICFGNode, ICFGNode> edges,
        BinaryRelation.Immutable<IBasicBlock, IBasicBlock> blockEdges,
        Set<IBasicBlock> startBlocks, Set<IBasicBlock> endBlocks,
        Set<ICFGNode> startNodes, Set<ICFGNode> endNodes, Set<ICFGNode> entryNodes, Set<ICFGNode> exitNodes,
        Set<ICFGNode> normalNodes) {
        return new ControlFlowGraph(edges, blockEdges, startBlocks, endBlocks, startNodes, endNodes, entryNodes,
            exitNodes, normalNodes);
    }
}
