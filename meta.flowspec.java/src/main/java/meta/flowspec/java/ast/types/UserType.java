package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class UserType extends SimpleType<IStrategoTerm> {
    public final String typeName;

    public UserType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("UserType", 1), factory.makeString(typeName));
    }
    
    public Lattice<IStrategoTerm> getLattice() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static class Utils {
        public static Optional<UserType> match(IStrategoTerm term) {
            return MatchTerm.applChildren(new StrategoConstructor("UserType", 1), term)
                    .flatMap(children -> MatchTerm.string(children[0]).map(UserType::new));
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
        UserType other = (UserType) obj;
        if (typeName == null) {
            if (other.typeName != null)
                return false;
        } else if (!typeName.equals(other.typeName))
            return false;
        return true;
    }
    
}
