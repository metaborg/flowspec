package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;

import mb.nabl2.terms.matching.TermMatch.IMatcher;

@Immutable
public abstract class FunctionInfo {
    public static IMatcher<FunctionInfo> match() {
        return M.tuple0(t -> of());
    }

    public static FunctionInfo of() {
        return ImmutableFunctionInfo.of();
    }
}
