package mb.flowspec.runtime.interpreter.values;

import java.util.Arrays;

import org.spoofax.interpreter.terms.IStrategoNamed;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Set;
import mb.flowspec.terms.B;
import mb.flowspec.terms.IStrategoAppl2;

public interface ISet<K extends IStrategoTerm> extends IStrategoAppl2 {
    public static final String NAME = "Set";
    public static final int ARITY = 1;

    Set.Immutable<K> getSet();

    @Override default String getName() {
        return NAME;
    }

    @Override default int getSubtermCount() {
        return ARITY;
    }

    @Override default IStrategoTerm[] getAllSubterms() {
        IStrategoTerm[] terms = getSet().stream().toArray(i -> new IStrategoTerm[i]);
        return new IStrategoTerm[] { B.list(terms) };
    }

    @Override default boolean match(IStrategoTerm second) {
        if(second == this) {
            return true;
        }
        if(second == null) {
            return false;
        }
        if(second instanceof ISet) {
            return ((ISet<?>) second).getSet().equals(this.getSet());
        }
        if(second instanceof IStrategoNamed) {
            return ((IStrategoNamed) second).getName().equals(getName())
                && Arrays.equals(getAllSubterms(), second.getAllSubterms());
        }
        return false;
    }
}