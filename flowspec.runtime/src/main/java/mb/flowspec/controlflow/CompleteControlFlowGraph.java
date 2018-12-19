package mb.flowspec.controlflow;

import java.io.Serializable;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Parameter;
import org.metaborg.util.iterators.Iterables2;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.flowspec.graph.Algorithms;
import mb.flowspec.terms.TermIndex;

@org.immutables.value.Value.Immutable
public abstract class CompleteControlFlowGraph
        implements ICompleteControlFlowGraph.Immutable, Serializable {
    @Override
    @Lazy
    public Set.Immutable<ICFGNode> nodes() {
        Set.Transient<ICFGNode> allNodes = Set.Transient.of();
        allNodes.__insertAll(normalNodes());
        allNodes.__insertAll(startNodes());
        allNodes.__insertAll(endNodes());
        allNodes.__insertAll(entryNodes());
        allNodes.__insertAll(exitNodes());
        return allNodes.freeze();
    }

    @Override
    @Parameter
    public abstract BinaryRelation.Immutable<ICFGNode, ICFGNode> edges();

    @Override
    @Parameter
    public abstract Set.Immutable<ICFGNode> startNodes();

    @Override
    @Parameter
    public abstract Set.Immutable<ICFGNode> endNodes();

    @Override
    @Parameter
    public abstract Set.Immutable<ICFGNode> entryNodes();

    @Override
    @Parameter
    public abstract Set.Immutable<ICFGNode> exitNodes();

    @Override
    @Parameter
    public abstract Set.Immutable<ICFGNode> normalNodes();

    @Override
    @Auxiliary
    @Parameter
    public abstract Set.Immutable<ICFGNode> unreachableNodes();

    @Override
    @Auxiliary
    @Parameter
    public abstract Set.Immutable<ICFGNode> deadEndNodes();

    @Override
    @Auxiliary
    @Parameter
    public abstract Iterable<java.util.Set<ICFGNode>> topoSCCs();

    @Override
    @Auxiliary
    @Parameter
    public abstract Iterable<java.util.Set<ICFGNode>> revTopoSCCs();

    @Override
    @Lazy
    public Map.Immutable<TermIndex, ICFGNode> startNodeMap() {
        return ICompleteControlFlowGraph.Immutable.super.startNodeMap();
    }

    @Override
    @Lazy
    public Map.Immutable<TermIndex, ICFGNode> endNodeMap() {
        return ICompleteControlFlowGraph.Immutable.super.endNodeMap();
    }

    @Override
    @Lazy
    public Map.Immutable<TermIndex, ICFGNode> normalNodeMap() {
        return ICompleteControlFlowGraph.Immutable.super.normalNodeMap();
    }
    
    public static ICompleteControlFlowGraph.Immutable of() {
        return ImmutableCompleteControlFlowGraph.of(BinaryRelation.Immutable.of(), Set.Immutable.of(),
                Set.Immutable.of(), Set.Immutable.of(), Set.Immutable.of(), Set.Immutable.of(), Set.Immutable.of(),
                Set.Immutable.of(), Iterables2.empty(), Iterables2.empty());
    }

    public static ICompleteControlFlowGraph.Immutable of(Set.Immutable<ICFGNode> normalNodes,
            BinaryRelation.Immutable<ICFGNode, ICFGNode> edges, Set.Immutable<ICFGNode> startNodes, Set.Immutable<ICFGNode> endNodes,
            Set.Immutable<ICFGNode> entryNodes, Set.Immutable<ICFGNode> exitNodes) {
        /*
         * NOTE: can we do better? SCCs are the same, topo order can be reversed, just
         * the order within the SCCs needs to be different. Perhaps faster to do
         * ordering within SCCs as post processing?
         */

        Set.Immutable<ICFGNode> allNodes = normalNodes.__insertAll(startNodes).__insertAll(endNodes);
        Algorithms.TopoSCCResult<ICFGNode> result = Algorithms.topoSCCs(allNodes, startNodes, endNodes, edges);

        return ImmutableCompleteControlFlowGraph.of(edges, startNodes, endNodes, entryNodes, exitNodes, normalNodes,
                result.unreachables(), result.deadEnds(), result.topoSCCs(), result.revTopoSCCs());
    }
}
