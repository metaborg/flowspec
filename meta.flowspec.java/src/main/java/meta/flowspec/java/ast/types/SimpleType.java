package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

import meta.flowspec.java.ast.OptionalUtils;

public abstract class SimpleType extends Type {
    public static class Utils {
        public static Optional<SimpleType> match(IStrategoTerm term) {
            return OptionalUtils.orElse(Name.Utils.match(term),
                    OptionalUtils.orElse(Term.Utils.match(term), UserType.Utils.match(term)));
        }
    }
}
