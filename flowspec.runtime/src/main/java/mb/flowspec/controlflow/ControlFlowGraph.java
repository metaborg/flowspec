package mb.flowspec.controlflow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

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
    private final int hashCode;

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
        this.hashCode = computeHashCode();
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

    /**
     * This instance is equal to all instances of {@code CompleteControlFlowGraph} that have equal attribute values.
     * 
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */
    @Override public boolean equals(@Nullable Object another) {
        if(this == another)
            return true;
        return another instanceof ControlFlowGraph && equalTo((ControlFlowGraph) another);
    }

    private boolean equalTo(ControlFlowGraph another) {
        if(hashCode != another.hashCode)
            return false;
        return edges.equals(another.edges) && startNodes.equals(another.startNodes) && endNodes.equals(another.endNodes)
            && entryNodes.equals(another.entryNodes) && exitNodes.equals(another.exitNodes)
            && normalNodes.equals(another.normalNodes);
    }

    /**
     * Returns a precomputed-on-construction hash code from attributes: {@code edges}, {@code startNodes},
     * {@code endNodes}, {@code entryNodes}, {@code exitNodes}, {@code normalNodes}.
     * 
     * @return hashCode value
     */
    @Override public int hashCode() {
        return hashCode;
    }

    private int computeHashCode() {
        int h = 5381;
        h += (h << 5) + blockEdges.hashCode();
        h += (h << 5) + startBlocks.hashCode();
        h += (h << 5) + endBlocks.hashCode();
        h += (h << 5) + edges.hashCode();
        h += (h << 5) + startNodes.hashCode();
        h += (h << 5) + endNodes.hashCode();
        h += (h << 5) + entryNodes.hashCode();
        h += (h << 5) + exitNodes.hashCode();
        h += (h << 5) + normalNodes.hashCode();
        return h;
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
