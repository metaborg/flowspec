package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class Origin extends SimpleType<IStrategoTerm> {
    public static Origin instance = new Origin();

    private Origin() {
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Origin", 0));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Lattice<IStrategoTerm> getLattice() {
        return Lattice.withoutOrder;
    }

    public static class Utils {
        public static Optional<Origin> match(IStrategoTerm term) {
            return MatchTerm.application(new StrategoConstructor("Origin", 0), term).map(_a -> Origin.instance);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Origin";
    }
}
