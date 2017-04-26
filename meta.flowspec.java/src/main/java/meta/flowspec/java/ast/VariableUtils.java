package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.stratego.MatchTerm;

public class VariableUtils {
    public static Optional<Variable> fromIStrategoTerm(IStrategoTerm term) {
        return MatchTerm.applChildren(new StrategoConstructor("Variable", 2), term).flatMap(children -> MatchTerm
                .integer(children[0]).flatMap(i1 -> MatchTerm.integer(children[1]).map(i2 -> new Variable(i1, i2))));
    }
}
