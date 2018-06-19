package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import mb.nabl2.terms.matching.TermMatch.IMatcher;

@Immutable
public abstract class SetType extends Type {
    @Parameter abstract Type key();

    public static IMatcher<SetType> match() {
        return M.appl1("Set", Type.matchType(), (appl, key) -> ImmutableSetType.of(key));
    }
}
