package mb.flowspec.runtime.interpreter.values;

import java.util.Arrays;

import org.spoofax.interpreter.terms.IStrategoNamed;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import mb.flowspec.terms.B;
import mb.flowspec.terms.IStrategoAppl2;

public interface IMap<K extends IStrategoTerm, V extends IStrategoTerm> extends IStrategoAppl2 {
    Map.Immutable<K, V> getMap();

    @Override default String getName() {
        return "Map";
    }

    @Override default int getSubtermCount() {
        return 1;
    }

    @Override default IStrategoTerm[] getAllSubterms() {
        IStrategoTerm[] terms =
            getMap().entrySet().stream().map(e -> B.tuple(e.getKey(), e.getValue())).toArray(i -> new IStrategoTerm[i]);
        return new IStrategoTerm[] { B.list(terms) };
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