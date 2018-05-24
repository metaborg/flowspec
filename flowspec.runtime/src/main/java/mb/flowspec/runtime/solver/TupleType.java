package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import mb.nabl2.terms.matching.TermMatch.IMatcher;
import static mb.nabl2.terms.matching.TermMatch.M;

@Immutable
public abstract class TupleType extends Type {
    @Parameter abstract Type left();
    @Parameter abstract Type right();

    public static IMatcher<TupleType> match() {
        return M.appl2("Tuple", Type.matchType(), Type.matchType(), (appl, left, right) -> ImmutableTupleType.of(left, right));
    }

}
