package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.stratego.MatchTerm;

public class ConditionUtils {
    public static Optional<Condition> fromIStrategoTerm(IStrategoTerm term) {
        return MatchTerm.applChildren(new StrategoConstructor("HasProp", 3), term).flatMap(children -> VariableUtils
                .fromIStrategoTerm(children[0]).flatMap(lhs -> MatchTerm.string(children[1]).flatMap(
                        rel -> VariableUtils.fromIStrategoTerm(children[2]).map(rhs -> new Condition(lhs, rel, rhs)))));
    }
}