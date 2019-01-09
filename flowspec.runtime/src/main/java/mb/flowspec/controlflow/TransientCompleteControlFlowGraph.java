package mb.flowspec.controlflow;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

import io.usethesource.capsule.BinaryRelation;

public class TransientCompleteControlFlowGraph implements ICompleteControlFlowGraph.Transient {
    private final BinaryRelation.Transient<ICFGNode, ICFGNode> edges;
    private final Set<ICFGNode> startNodes;
    private final Set<ICFGNode> normalNodes;
    private final Set<ICFGNode> endNodes;
    private final Set<ICFGNode> entryNodes;
    private final Set<ICFGNode> exitNodes;
    private transient final int hashCode;

    private TransientCompleteControlFlowGraph(
        BinaryRelation.Transient<ICFGNode, ICFGNode> edges,
        Set<ICFGNode> startNodes,
        Set<ICFGNode> normalNodes,
        Set<ICFGNode> endNodes,
        Set<ICFGNode> entryNodes,
        Set<ICFGNode> exitNodes) {
      this.edges = edges;
      this.startNodes = startNodes;
      this.normalNodes = normalNodes;
      this.endNodes = endNodes;
      this.entryNodes = entryNodes;
      this.exitNodes = exitNodes;
      this.hashCode = computeHashCode();
    }

    /**
     * @return The value of the {@code edges} attribute
     */
    @Override
    public BinaryRelation.Transient<ICFGNode, ICFGNode> edges() {
      return edges;
    }

    /**
     * @return The value of the {@code startNodes} attribute
     */
    @Override
    public Set<ICFGNode> startNodes() {
      return startNodes;
    }

    /**
     * @return The value of the {@code normalNodes} attribute
     */
    @Override
    public Set<ICFGNode> normalNodes() {
      return normalNodes;
    }

    /**
     * @return The value of the {@code endNodes} attribute
     */
    @Override
    public Set<ICFGNode> endNodes() {
      return endNodes;
    }

    /**
     * @return The value of the {@code entryNodes} attribute
     */
    @Override
    public Set<ICFGNode> entryNodes() {
      return entryNodes;
    }

    /**
     * @return The value of the {@code exitNodes} attribute
     */
    @Override
    public Set<ICFGNode> exitNodes() {
      return exitNodes;
    }

    /**
     * This instance is equal to all instances of {@code TransientCompleteControlFlowGraph} that have equal attribute values.
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */
    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another) return true;
      return another instanceof TransientCompleteControlFlowGraph
          && equalTo((TransientCompleteControlFlowGraph) another);
    }

    private boolean equalTo(TransientCompleteControlFlowGraph another) {
      if (hashCode != another.hashCode) return false;
      return edges.equals(another.edges)
          && startNodes.equals(another.startNodes)
          && normalNodes.equals(another.normalNodes)
          && endNodes.equals(another.endNodes)
          && entryNodes.equals(another.entryNodes)
          && exitNodes.equals(another.exitNodes);
    }

    /**
     * Returns a precomputed-on-construction hash code from attributes: {@code edges}, {@code startNodes}, {@code normalNodes}, {@code endNodes}, {@code entryNodes}, {@code exitNodes}.
     * @return hashCode value
     */
    @Override
    public int hashCode() {
      return hashCode;
    }

    private int computeHashCode() {
      int h = 5381;
      h += (h << 5) + edges.hashCode();
      h += (h << 5) + startNodes.hashCode();
      h += (h << 5) + normalNodes.hashCode();
      h += (h << 5) + endNodes.hashCode();
      h += (h << 5) + entryNodes.hashCode();
      h += (h << 5) + exitNodes.hashCode();
      return h;
    }

    /**
     * Prints the immutable value {@code TransientCompleteControlFlowGraph} with attribute values.
     * @return A string representation of the value
     */
    @Override
    public String toString() {
      return MoreObjects.toStringHelper("TransientCompleteControlFlowGraph")
          .omitNullValues()
          .add("edges", edges)
          .add("startNodes", startNodes)
          .add("normalNodes", normalNodes)
          .add("endNodes", endNodes)
          .add("entryNodes", entryNodes)
          .add("exitNodes", exitNodes)
          .toString();
    }

    public Set<ICFGNode> nodes() {
        Set<ICFGNode> allNodes = new HashSet<>(normalNodes().size() + startNodes().size() + endNodes().size() + entryNodes().size() + exitNodes().size());
        allNodes.addAll(normalNodes());
        allNodes.addAll(startNodes());
        allNodes.addAll(endNodes());
        allNodes.addAll(entryNodes());
        allNodes.addAll(exitNodes());
        return Collections.unmodifiableSet(allNodes);
    }

    public static ICompleteControlFlowGraph.Transient of() {
        return new TransientCompleteControlFlowGraph(BinaryRelation.Transient.of(), new HashSet<>(),
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    }
}
