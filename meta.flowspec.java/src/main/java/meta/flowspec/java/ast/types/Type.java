package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

import meta.flowspec.java.ast.OptionalUtils;
import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.ToIStrategoTerm;

public abstract class Type<L> implements ToIStrategoTerm {
    public static class Utils {
        @SuppressWarnings("rawtypes")
        public static Optional<Type> match(IStrategoTerm term) {
            return OptionalUtils.orElse(
                    OptionalUtils.orElse(OptionalUtils.orElse(SimpleType.Utils.match(term), Tuple.Utils.match(term)),
                            Set.Utils.match(term)),
                    Option.Utils.match(term));
        }
    }
    
    public abstract Lattice<L> getLattice();
}
