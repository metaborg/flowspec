package mb.flowspec.runtime.interpreter.values;

import java.util.Arrays;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoNamed;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import mb.flowspec.terms.B;
import mb.flowspec.terms.IStrategoAppl2;
import org.spoofax.terms.TermList;

public interface IMap<K extends IStrategoTerm, V extends IStrategoTerm> extends IStrategoAppl2 {
    public static final String NAME = "Map";
    public static final int ARITY = 1;

    Map.Immutable<K, V> getMap();

    @Override default String getName() {
        return NAME;
    }

    @Override default int getSubtermCount() {
        return ARITY;
    }

    @Override default List<IStrategoTerm> getSubterms() {
        IStrategoTerm[] terms =
                getMap().entrySet().stream().map(e -> B.tuple(e.getKey(), e.getValue())).toArray(IStrategoTerm[]::new);
        return TermList.of(terms);
    }

    @Override default boolean match(IStrategoTerm second) {
        if(second == this) {
            return true;
        }
        if(second == null) {
            return false;
        }
        if(second instanceof IMap) {
            return ((IMap<?, ?>) second).getMap().equals(this.getMap());
        }
        if(second instanceof IStrategoNamed) {
            return ((IStrategoNamed) second).getName().equals(getName())
                && Arrays.equals(getAllSubterms(), second.getAllSubterms());
        }
        return false;
    }
}