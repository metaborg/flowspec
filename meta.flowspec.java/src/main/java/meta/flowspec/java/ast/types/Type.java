package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

import meta.flowspec.java.ast.OptionalUtils;
import meta.flowspec.java.stratego.ToIStrategoTerm;

public abstract class Type implements ToIStrategoTerm {
    public static class Utils {
        public static Optional<Type> match(IStrategoTerm term) {
            return OptionalUtils.orElse(SimpleType.Utils.match(term), OptionalUtils.orElse(Tuple.Utils.match(term),
                    OptionalUtils.orElse(Set.Utils.match(term), Option.Utils.match(term))));
        }
    }
}
