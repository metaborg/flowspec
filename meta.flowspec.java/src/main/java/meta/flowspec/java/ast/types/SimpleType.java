package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

import meta.flowspec.java.ast.OptionalUtils;

public abstract class SimpleType<L> extends Type<L> {
    public static class Utils {
        @SuppressWarnings("rawtypes")
        public static Optional<SimpleType> match(IStrategoTerm term) {
            return OptionalUtils.orElse(Name.Utils.match(term),
                    OptionalUtils.orElse(Origin.Utils.match(term), OptionalUtils.orElse(CFGNode.Utils.match(term),
                            OptionalUtils.orElse(UserType.Utils.match(term), Sort.Utils.match(term)))));
        }
    }
}
