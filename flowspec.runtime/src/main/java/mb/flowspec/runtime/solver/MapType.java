package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import mb.nabl2.terms.matching.TermMatch.IMatcher;

@Immutable
public abstract class MapType extends Type {
    @Parameter abstract Type key();
    @Parameter abstract Type value();

    public static IMatcher<MapType> match() {
        return M.appl2("Map", Type.matchType(), Type.matchType(), (appl, key, value) -> ImmutableMapType.of(key, value));
    }
}
