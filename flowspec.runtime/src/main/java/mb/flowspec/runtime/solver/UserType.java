package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import mb.nabl2.terms.matching.TermMatch.IMatcher;
import static mb.nabl2.terms.matching.TermMatch.M;

@Immutable
public abstract class UserType extends Type {
    @Parameter
    abstract String name();

    @Parameter
    abstract Type[] params();

    public static IMatcher<UserType> match() {
        return M.appl2("UserType", M.stringValue(), M.listElems(Type.matchType()),
                (appl, name, args) -> ImmutableUserType.of(name, args.toArray(new Type[0])));
    }
}
