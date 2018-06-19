package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Optional;

import mb.nabl2.terms.matching.TermMatch.IMatcher;

public abstract class Type {
    public static IMatcher<Type> matchType() {
        return (term, unifier) -> Optional.of(M.cases(
            TupleType.match(),
            MapType.match(),
            SetType.match(),
            UserType.match(),
            SimpleType.matchSimpleType()
        ).match(term, unifier)
         .orElseThrow(() -> new ParseException("Parse error on reading expression " + term)));
    }
}
