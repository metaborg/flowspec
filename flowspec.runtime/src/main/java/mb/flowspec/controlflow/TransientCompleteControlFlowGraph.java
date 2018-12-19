package mb.flowspec.controlflow;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Set;

@Value.Immutable
public abstract class TransientCompleteControlFlowGraph implements ICompleteControlFlowGraph.Transient {

    @Override
    @Parameter
    public abstract BinaryRelation.Transient<ICFGNode, ICFGNode> edges();

    @Override
    @Parameter
    public abstract Set.Transient<ICFGNode> startNodes();

    @Override
    @Parameter
    public abstract Set.Transient<ICFGNode> normalNodes();

    @Override
    @Parameter
    public abstract Set.Transient<ICFGNode> endNodes();

    @Override
    @Parameter
    public abstract Set.Transient<ICFGNode> entryNodes();

    @Override
    @Parameter
    public abstract Set.Transient<ICFGNode> exitNodes();

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

    public static ICompleteControlFlowGraph.Transient of() {
        return ImmutableTransientCompleteControlFlowGraph.of(BinaryRelation.Transient.of(), Set.Transient.of(),
                Set.Transient.of(), Set.Transient.of(), Set.Transient.of(), Set.Transient.of());
    }
}
