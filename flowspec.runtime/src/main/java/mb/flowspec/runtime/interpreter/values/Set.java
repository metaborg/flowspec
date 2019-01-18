package mb.flowspec.runtime.interpreter.values;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Set.Immutable;

public class Set<K extends IStrategoTerm> implements ISet<K>, Serializable {
    /**
     * Stratego compiler assumes constructors are maximally shared and does identity comparison.
     * So we initialize the constructors at runtime...
     */
    public static IStrategoConstructor CONS = null;

    private final Immutable<K> set;

    public Set() {
        this(Immutable.of());
    }

    public Set(Immutable<K> set) {
        this.set = set;
    }

    @Override public IStrategoConstructor getConstructor() {
        if(CONS != null) {
            return CONS;
        } else {
            return ISet.super.getConstructor();
        }
    }

    @Override
    public Immutable<K> getSet() {
        return set;
    }

    @Override
    public String toString() {
        if (getSet() == null) {
            return "null";
        } else {
            return getSet().toString();
        }
    }

    @Override
    public int hashCode() {
        return getSet().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ISet))
            return false;
        @SuppressWarnings("rawtypes")
        ISet other = (ISet) obj;
        if (getSet() == null) {
            if (other.getSet() != null)
                return false;
        } else if (!getSet().equals(other.getSet()))
            return false;
        return getSet().equals(other.getSet());
    }
}
