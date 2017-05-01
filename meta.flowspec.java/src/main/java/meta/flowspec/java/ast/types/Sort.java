package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class Sort extends SimpleType<IStrategoTerm> {
    public final String sortName;
    
    public Sort(String sortName) {
        this.sortName = sortName;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Sort", 1), factory.makeString(sortName));
    }

    public static class Utils {
        public static Optional<Sort> match(IStrategoTerm term) {
            return MatchTerm.applChildren(new StrategoConstructor("Sort", 1), term)
                    .flatMap(children -> MatchTerm.string(children[0]).map(Sort::new));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Lattice<IStrategoTerm> getLattice() {
        return Lattice.withoutOrder;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sortName == null) ? 0 : sortName.hashCode());
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
        Sort other = (Sort) obj;
        if (sortName == null) {
            if (other.sortName != null)
                return false;
        } else if (!sortName.equals(other.sortName))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Sort [sortName=" + sortName + "]";
    }
    
    
}
