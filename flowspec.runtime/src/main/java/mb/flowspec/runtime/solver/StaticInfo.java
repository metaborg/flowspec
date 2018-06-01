package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import mb.nabl2.terms.matching.TermMatch.IMatcher;

@Immutable
public abstract class StaticInfo {
    @Parameter public abstract TransferFunctionInfo transfers();
    @Parameter public abstract FunctionInfo functions();
    @Parameter public abstract LatticeInfo lattices();

    public StaticInfo addAll(StaticInfo other) {
        TransferFunctionInfo transfers = this.transfers().addAll(other.transfers());
        FunctionInfo functions = this.functions().addAll(other.functions());
        LatticeInfo lattices = this.lattices().addAll(other.lattices());
        return ImmutableStaticInfo.of(transfers, functions, lattices);
    }

    public static IMatcher<StaticInfo> match() {
        return (term, unifier) ->
            M.tuple3(M.term(), LatticeInfo.match(), FunctionInfo.match(), (t, tf, l, f) ->
                TransferFunctionInfo.match(l)
                    .match(tf, unifier)
                    .map(tf1 -> (StaticInfo) ImmutableStaticInfo.of(tf1, f, l))
            )
            .flatMap(i -> i)
            .match(term, unifier);
    }

    public static StaticInfo of() {
        return ImmutableStaticInfo.of(ImmutableTransferFunctionInfo.of(), ImmutableFunctionInfo.of(), ImmutableLatticeInfo.of());
    }
}
