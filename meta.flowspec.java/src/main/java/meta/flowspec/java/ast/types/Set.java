package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.pcollections.PSet;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.lattice.CompleteLattice;
import meta.flowspec.java.lattice.FullSetLattice;
import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class Set<L> extends Type<PSet<L>> {
    public final Type<L> element;

    public Set(Type<L> element) {
        this.element = element;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Set", 1), element.toIStrategoTerm(factory));
    }

    public static class Utils {
        @SuppressWarnings("unchecked")
        public static <L> Optional<Set<L>> match(IStrategoTerm term) {
            return MatchTerm.applChildren(new StrategoConstructor("Set", 1), term)
                    .flatMap(children -> Type.Utils.match(children[0]).map(Set::new));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Lattice<PSet<L>> getLattice() {
        return (Lattice<PSet<L>>) this.internalGetLattice();
    }
    
    @SuppressWarnings("rawtypes")
    public CompleteLattice internalGetLattice() {
        return new FullSetLattice<L>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
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
        Set<L> other = (Set<L>) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Set [element=" + element + "]";
    }

}
