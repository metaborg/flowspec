package mb.flowspec.controlflow;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.metaborg.util.iterators.Iterables2;

import com.google.common.base.MoreObjects;

import io.usethesource.capsule.BinaryRelation;
import mb.flowspec.graph.Algorithms;
import mb.flowspec.terms.TermIndex;

public class CompleteControlFlowGraph
        implements ICompleteControlFlowGraph.Immutable, Serializable {
    private final BinaryRelation.Immutable<ICFGNode, ICFGNode> edges;
    private final Set<ICFGNode> startNodes;
    private final Set<ICFGNode> endNodes;
    private final Set<ICFGNode> entryNodes;
    private final Set<ICFGNode> exitNodes;
    private final Set<ICFGNode> normalNodes;
    private final Set<ICFGNode> unreachableNodes;
    private final java.lang.Iterable<Set<ICFGNode>> topoSCCs;
    private final java.lang.Iterable<Set<ICFGNode>> revTopoSCCs;
    private final int hashCode;

    private CompleteControlFlowGraph(
        BinaryRelation.Immutable<ICFGNode, ICFGNode> edges,
        Set<ICFGNode> startNodes,
        Set<ICFGNode> endNodes,
        Set<ICFGNode> entryNodes,
        Set<ICFGNode> exitNodes,
        Set<ICFGNode> normalNodes,
        Set<ICFGNode> unreachableNodes,
        java.lang.Iterable<Set<ICFGNode>> topoSCCs,
        java.lang.Iterable<Set<ICFGNode>> revTopoSCCs) {
      this.edges = edges;
      this.startNodes = startNodes;
      this.endNodes = endNodes;
      this.entryNodes = entryNodes;
      this.exitNodes = exitNodes;
      this.normalNodes = normalNodes;
      this.unreachableNodes = unreachableNodes;
      this.topoSCCs = topoSCCs;
      this.revTopoSCCs = revTopoSCCs;
      this.hashCode = computeHashCode();
    }

    /**
     * @return The value of the {@code edges} attribute
     */
    @Override
    public BinaryRelation.Immutable<ICFGNode, ICFGNode> edges() {
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
     * @return The value of the {@code normalNodes} attribute
     */
    @Override
    public Set<ICFGNode> normalNodes() {
      return normalNodes;
    }

    /**
     * @return The value of the {@code unreachableNodes} attribute
     */
    @Override
    public Set<ICFGNode> unreachableNodes() {
      return unreachableNodes;
    }

    /**
     * @return The value of the {@code topoSCCs} attribute
     */
    @Override
    public java.lang.Iterable<Set<ICFGNode>> topoSCCs() {
      return topoSCCs;
    }

    /**
     * @return The value of the {@code revTopoSCCs} attribute
     */
    @Override
    public java.lang.Iterable<Set<ICFGNode>> revTopoSCCs() {
      return revTopoSCCs;
    }

    /**
     * This instance is equal to all instances of {@code CompleteControlFlowGraph} that have equal attribute values.
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */
    @Override
    public boolean equals(@Nullable Object another) {
      if (this == another) return true;
      return another instanceof CompleteControlFlowGraph
          && equalTo((CompleteControlFlowGraph) another);
    }

    private boolean equalTo(CompleteControlFlowGraph another) {
      if (hashCode != another.hashCode) return false;
      return edges.equals(another.edges)
          && startNodes.equals(another.startNodes)
          && endNodes.equals(another.endNodes)
          && entryNodes.equals(another.entryNodes)
          && exitNodes.equals(another.exitNodes)
          && normalNodes.equals(another.normalNodes);
    }

    /**
     * Returns a precomputed-on-construction hash code from attributes: {@code edges}, {@code startNodes}, {@code endNodes}, {@code entryNodes}, {@code exitNodes}, {@code normalNodes}.
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
      h += (h << 5) + endNodes.hashCode();
      h += (h << 5) + entryNodes.hashCode();
      h += (h << 5) + exitNodes.hashCode();
      h += (h << 5) + normalNodes.hashCode();
      return h;
    }

    /**
     * Prints the immutable value {@code CompleteControlFlowGraph} with attribute values.
     * @return A string representation of the value
     */
    @Override
    public String toString() {
      return MoreObjects.toStringHelper("CompleteControlFlowGraph")
          .omitNullValues()
          .add("edges", edges)
          .add("startNodes", startNodes)
          .add("endNodes", endNodes)
          .add("entryNodes", entryNodes)
          .add("exitNodes", exitNodes)
          .add("normalNodes", normalNodes)
          .toString();
    }

    private transient volatile long lazyInitBitmap;

    private static final long NODES_LAZY_INIT_BIT = 0x1L;

    private transient Set<ICFGNode> nodes;

    /**
     * {@inheritDoc}
     * <p>
     * Returns a lazily initialized value of the {@link CompleteControlFlowGraph#nodes() nodes} attribute.
     * Initialized once and only once and stored for subsequent access with proper synchronization.
     * @return A lazily initialized value of the {@code nodes} attribute
     */
    @Override
    public Set<ICFGNode> nodes() {
      if ((lazyInitBitmap & NODES_LAZY_INIT_BIT) == 0) {
        synchronized (this) {
          if ((lazyInitBitmap & NODES_LAZY_INIT_BIT) == 0) {
            this.nodes = Objects.requireNonNull(computeNodes(), "nodes");
            lazyInitBitmap |= NODES_LAZY_INIT_BIT;
          }
        }
      }
      return nodes;
    }

    public Set<ICFGNode> computeNodes() {
        Set<ICFGNode> allNodes = new HashSet<>(normalNodes().size() + startNodes().size() + endNodes().size() + entryNodes().size() + exitNodes().size());
        allNodes.addAll(normalNodes());
        allNodes.addAll(startNodes());
        allNodes.addAll(endNodes());
        allNodes.addAll(entryNodes());
        allNodes.addAll(exitNodes());
        return Collections.unmodifiableSet(allNodes);
    }

    private static final long START_NODE_MAP_LAZY_INIT_BIT = 0x2L;

    private transient Map<TermIndex, ICFGNode> startNodeMap;

    /**
     * {@inheritDoc}
     * <p>
     * Returns a lazily initialized value of the {@link CompleteControlFlowGraph#startNodeMap() startNodeMap} attribute.
     * Initialized once and only once and stored for subsequent access with proper synchronization.
     * @return A lazily initialized value of the {@code startNodeMap} attribute
     */
    @Override
    public Map<TermIndex, ICFGNode> startNodeMap() {
      if ((lazyInitBitmap & START_NODE_MAP_LAZY_INIT_BIT) == 0) {
        synchronized (this) {
          if ((lazyInitBitmap & START_NODE_MAP_LAZY_INIT_BIT) == 0) {
            this.startNodeMap = Objects.requireNonNull(ICompleteControlFlowGraph.Immutable.super.startNodeMap(), "startNodeMap");
            lazyInitBitmap |= START_NODE_MAP_LAZY_INIT_BIT;
          }
        }
      }
      return startNodeMap;
    }

    private static final long END_NODE_MAP_LAZY_INIT_BIT = 0x4L;

    private transient Map<TermIndex, ICFGNode> endNodeMap;

    /**
     * {@inheritDoc}
     * <p>
     * Returns a lazily initialized value of the {@link CompleteControlFlowGraph#endNodeMap() endNodeMap} attribute.
     * Initialized once and only once and stored for subsequent access with proper synchronization.
     * @return A lazily initialized value of the {@code endNodeMap} attribute
     */
    @Override
    public Map<TermIndex, ICFGNode> endNodeMap() {
      if ((lazyInitBitmap & END_NODE_MAP_LAZY_INIT_BIT) == 0) {
        synchronized (this) {
          if ((lazyInitBitmap & END_NODE_MAP_LAZY_INIT_BIT) == 0) {
            this.endNodeMap = Objects.requireNonNull(ICompleteControlFlowGraph.Immutable.super.endNodeMap(), "endNodeMap");
            lazyInitBitmap |= END_NODE_MAP_LAZY_INIT_BIT;
          }
        }
      }
      return endNodeMap;
    }

    private static final long ENTRY_NODE_MAP_LAZY_INIT_BIT = 0x8L;

    private transient Map<TermIndex, ICFGNode> entryNodeMap;

    /**
     * {@inheritDoc}
     * <p>
     * Returns a lazily initialized value of the {@link CompleteControlFlowGraph#entryNodeMap() entryNodeMap} attribute.
     * Initialized once and only once and stored for subsequent access with proper synchronization.
     * @return A lazily initialized value of the {@code entryNodeMap} attribute
     */
    @Override
    public Map<TermIndex, ICFGNode> entryNodeMap() {
      if ((lazyInitBitmap & ENTRY_NODE_MAP_LAZY_INIT_BIT) == 0) {
        synchronized (this) {
          if ((lazyInitBitmap & ENTRY_NODE_MAP_LAZY_INIT_BIT) == 0) {
            this.entryNodeMap = Objects.requireNonNull(ICompleteControlFlowGraph.Immutable.super.entryNodeMap(), "entryNodeMap");
            lazyInitBitmap |= ENTRY_NODE_MAP_LAZY_INIT_BIT;
          }
        }
      }
      return entryNodeMap;
    }

    private static final long EXIT_NODE_MAP_LAZY_INIT_BIT = 0x10L;

    private transient Map<TermIndex, ICFGNode> exitNodeMap;

    /**
     * {@inheritDoc}
     * <p>
     * Returns a lazily initialized value of the {@link CompleteControlFlowGraph#exitNodeMap() exitNodeMap} attribute.
     * Initialized once and only once and stored for subsequent access with proper synchronization.
     * @return A lazily initialized value of the {@code exitNodeMap} attribute
     */
    @Override
    public Map<TermIndex, ICFGNode> exitNodeMap() {
      if ((lazyInitBitmap & EXIT_NODE_MAP_LAZY_INIT_BIT) == 0) {
        synchronized (this) {
          if ((lazyInitBitmap & EXIT_NODE_MAP_LAZY_INIT_BIT) == 0) {
            this.exitNodeMap = Objects.requireNonNull(ICompleteControlFlowGraph.Immutable.super.exitNodeMap(), "exitNodeMap");
            lazyInitBitmap |= EXIT_NODE_MAP_LAZY_INIT_BIT;
          }
        }
      }
      return exitNodeMap;
    }

    private static final long NORMAL_NODE_MAP_LAZY_INIT_BIT = 0x20L;

    private transient Map<TermIndex, ICFGNode> normalNodeMap;

    /**
     * {@inheritDoc}
     * <p>
     * Returns a lazily initialized value of the {@link CompleteControlFlowGraph#normalNodeMap() normalNodeMap} attribute.
     * Initialized once and only once and stored for subsequent access with proper synchronization.
     * @return A lazily initialized value of the {@code normalNodeMap} attribute
     */
    @Override
    public Map<TermIndex, ICFGNode> normalNodeMap() {
      if ((lazyInitBitmap & NORMAL_NODE_MAP_LAZY_INIT_BIT) == 0) {
        synchronized (this) {
          if ((lazyInitBitmap & NORMAL_NODE_MAP_LAZY_INIT_BIT) == 0) {
            this.normalNodeMap = Objects.requireNonNull(ICompleteControlFlowGraph.Immutable.super.normalNodeMap(), "normalNodeMap");
            lazyInitBitmap |= NORMAL_NODE_MAP_LAZY_INIT_BIT;
          }
        }
      }
      return normalNodeMap;
    }

    public static ICompleteControlFlowGraph.Immutable of() {
        return new CompleteControlFlowGraph(BinaryRelation.Immutable.of(), Collections.emptySet(),
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
                Collections.emptySet(), Iterables2.empty());
    }

    public static ICompleteControlFlowGraph.Immutable of(Set<ICFGNode> normalNodes,
            BinaryRelation.Immutable<ICFGNode, ICFGNode> edges, Set<ICFGNode> startNodes, Set<ICFGNode> endNodes,
            Set<ICFGNode> entryNodes, Set<ICFGNode> exitNodes) {
        /*
         * NOTE: can we do better? SCCs are the same, topo order can be reversed, just
         * the order within the SCCs needs to be different. Perhaps faster to do
         * ordering within SCCs as post processing?
         */

        Set<ICFGNode> allNodes = new HashSet<>(normalNodes.size() + startNodes.size() + endNodes.size());
        allNodes.addAll(normalNodes);
        allNodes.addAll(startNodes);
        allNodes.addAll(endNodes);
        allNodes = Collections.unmodifiableSet(allNodes);
        Algorithms.TopoSCCResult<ICFGNode> result = Algorithms.topoSCCs(allNodes, startNodes, endNodes, edges);

        return new CompleteControlFlowGraph(edges, startNodes, endNodes, entryNodes, exitNodes, normalNodes,
                result.unreachables, result.topoSCCs, result.revTopoSCCs);
    }
}
