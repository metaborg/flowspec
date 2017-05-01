package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class Option<L> extends Type<Optional<L>> {
    public final Type<L> element;
    
    public Option(Type<L> element) {
        this.element = element;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Option", 1), element.toIStrategoTerm(factory));
    }

    @Override
    public Lattice<Optional<L>> getLattice() {
        return new Lattice<Optional<L>>() {
            @Override
            public Optional<Optional<L>> partial_glb(Optional<L> one, Optional<L> other) {
                if (one == other) {
                    return Optional.of(one);
                } else {
                    return one.flatMap(l -> other.map(r -> Option.this.element.getLattice().partial_glb(l, r)));
                }
            }

            @Override
            public Optional<Optional<L>> partial_lub(Optional<L> one, Optional<L> other) {
                if (one == other) {
                    return Optional.of(one);
                } else {
                    return one.flatMap(l -> other.map(r -> Option.this.element.getLattice().partial_lub(l, r)));
                }
            }
        };
    }

    public static class Utils {
        @SuppressWarnings("unchecked")
        public static <L> Optional<Option<L>> match(IStrategoTerm term) {
            return MatchTerm.applChildren(new StrategoConstructor("Set", 1), term)
                    .flatMap(children -> Type.Utils.match(children[0]).map(Option::new));
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Option [element=" + element + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Option<L> other = (Option<L>) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
        return true;
    }
    
}
