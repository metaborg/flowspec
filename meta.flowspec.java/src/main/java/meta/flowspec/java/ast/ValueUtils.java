package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

public class ValueUtils {
    public static Optional<Value> fromIStrategoTerm(IStrategoTerm term) {
        return OptionalUtils.orElse(TermIndexUtils.fromIStrategoTerm(term), VariableUtils.fromIStrategoTerm(term));
    }
}