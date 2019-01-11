package mb.flowspec.controlflow;

import java.io.Serializable;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.metaborg.util.Ref;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import mb.nabl2.solver.ISolution;
import mb.nabl2.util.Tuple2;

@Immutable
public abstract class FlowSpecSolution implements IFlowSpecSolution, Serializable {
    @Override
    @Parameter
    public abstract ISolution solution();
    
    @Override
    @Parameter
    public abstract IControlFlowGraph controlFlowGraph();

    @Override
    @Parameter
    public abstract Map.Immutable<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls();

    @Override
    @Parameter
    public abstract Map.Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties();

    @Override
    @Parameter
    public abstract Map.Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties();

    public static IFlowSpecSolution of(ISolution solution, IControlFlowGraph controlFlowGraph, Map.Immutable<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls) {
        return ImmutableFlowSpecSolution.of(solution, controlFlowGraph, tfAppls, Map.Immutable.of(), Map.Immutable.of());
    }
}
