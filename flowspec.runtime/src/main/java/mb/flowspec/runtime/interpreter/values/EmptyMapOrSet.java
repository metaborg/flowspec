package mb.flowspec.runtime.interpreter.values;

import java.io.Serializable;
import java.util.Arrays;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.flowspec.terms.B;

public class EmptyMapOrSet<K extends IStrategoTerm, V extends IStrategoTerm> implements ISet<K>, IMap<K, V>, Serializable {

    private final Set.Immutable<K> set;
    private final Map.Immutable<K, V> map;

    public EmptyMapOrSet() {
        this.set = Set.Immutable.of();
        this.map = Map.Immutable.of();
    }

    @Override
    public Set.Immutable<K> getSet() {
        return set;
    }

    @Override
    public Map.Immutable<K, V> getMap() {
        return map;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(obj instanceof IMap) {
            @SuppressWarnings("rawtypes")
            IMap other = (IMap) obj;
            if(getMap() == null) {
                if(other.getMap() != null)
                    return false;
            } else if(!getMap().equals(other.getMap()))
                return false;
            return true;
        } else if(obj instanceof ISet) {
            @SuppressWarnings("rawtypes")
            ISet other = (ISet) obj;
            if(getSet() == null) {
                if(other.getSet() != null)
                    return false;
            } else if(!getSet().equals(other.getSet()))
                return false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return set.toString();
    }

    @Override public boolean match(IStrategoTerm second) {
        if(this == second) {
            return true;
        }
        if(null == second) {
            return false;
        }
        if(this.getTermType() == second.getTermType()) {
            IStrategoAppl appl = (IStrategoAppl) second;
            return this.getName().equals(appl.getName())
                && Arrays.equals(this.getAllSubterms(), second.getAllSubterms());
        }
        return false;
    }

    @Override public int getSubtermCount() {
        return 1;
    }

    @Override public IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[] { B.list() };
    }

    @Override public String getName() {
        return ISet.super.getName();
    }
}
