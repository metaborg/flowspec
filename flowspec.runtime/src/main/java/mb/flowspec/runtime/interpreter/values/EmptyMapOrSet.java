package mb.flowspec.runtime.interpreter.values;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.Multiset;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.Map;

public class EmptyMapOrSet<K extends ITerm, V extends ITerm> implements ISet<K>, IMap<K, V>, Serializable {

    private final Set.Immutable<K> set;
    private final Map.Immutable<K, V> map;
    private final ImmutableClassToInstanceMap<Object> attachments;

    public EmptyMapOrSet() {
        this.set = Set.Immutable.of();
        this.map = Map.Immutable.of();
        this.attachments = ImmutableClassToInstanceMap.builder().build();
    }

    public EmptyMapOrSet(ImmutableClassToInstanceMap<Object> attachments) {
        this.set = Set.Immutable.of();
        this.map = Map.Immutable.of();
        this.attachments = attachments;
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
    public ImmutableClassToInstanceMap<Object> getAttachments() {
        return attachments;
    }

    @Override
    public EmptyMapOrSet<K, V> withAttachments(ImmutableClassToInstanceMap<Object> value) {
        return new EmptyMapOrSet<>(value);
    }

    @Override
    public List<ITerm> getArgs() {
        return IMap.super.getArgs();
    }

    @Override
    public int getArity() {
        return IMap.super.getArity();
    }

    @Override
    public <T> T match(Cases<T> cases) {
        return IMap.super.match(cases);
    }

    @Override
    public boolean isGround() {
        return IMap.super.isGround();
    }

    @Override
    public Multiset<ITermVar> getVars() {
        return IMap.super.getVars();
    }

    @Override
    public <T, E extends Throwable> T matchOrThrow(CheckedCases<T, E> cases) throws E {
        return IMap.super.matchOrThrow(cases);
    }

    @Override
    public String getOp() {
        return IMap.super.getOp();
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
}
