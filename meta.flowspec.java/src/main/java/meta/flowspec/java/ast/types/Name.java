package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class Name extends SimpleType<IStrategoTerm> {
    public static Name instance = new Name();

    private Name() {
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Name", 0));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Lattice<IStrategoTerm> getLattice() {
        return Lattice.withoutOrder;
    }

    public static class Utils {
        public static Optional<Name> match(IStrategoTerm term) {
            return MatchTerm.application(new StrategoConstructor("Name", 0), term).map(_a -> Name.instance);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Name";
    }

}
