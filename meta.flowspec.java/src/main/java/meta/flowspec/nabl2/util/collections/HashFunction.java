package meta.flowspec.nabl2.util.collections;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import meta.flowspec.nabl2.util.collections.HashInverseFunction;
import meta.flowspec.nabl2.util.collections.IFunction;
import meta.flowspec.nabl2.util.collections.IInverseFunction;
import meta.flowspec.nabl2.util.collections.ISet;
import meta.flowspec.nabl2.util.collections.WrappedSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class HashFunction<K, V> implements IFunction.Mutable<K, V>, Serializable {

    private static final long serialVersionUID = 42L;

    private final Map<K, V> fwd;
    private final SetMultimap<V, K> bwd;

    HashFunction(Map<K, V> fwd, SetMultimap<V, K> bwd) {
        this.fwd = fwd;
        this.bwd = bwd;
    }

    @Override public IInverseFunction.Mutable<V, K> inverse() {
        return new HashInverseFunction<>(bwd, fwd);
    }

    @Override public boolean put(K key, V value) {
        if(fwd.containsKey(key)) {
            if(value.equals(fwd.get(key))) {
                return false;
            }
        }
        fwd.put(key, value);
        bwd.put(value, key);
        return true;
    }

    @Override public boolean remove(K key) {
        V value = fwd.remove(key);
        if(value != null) {
            bwd.remove(value, key);
            return true;
        }
        return false;
    }

    @Override public boolean containsKey(K key) {
        return fwd.containsKey(key);
    }

    @Override public boolean containsValue(V value) {
        return bwd.containsKey(value);
    }

    @Override public boolean containsEntry(K key, V value) {
        return bwd.containsEntry(value, key);
    }

    @Override public ISet<K> keySet() {
        return WrappedSet.of(fwd.keySet());
    }

    @Override public ISet<V> valueSet() {
        return WrappedSet.of(bwd.keySet());
    }

    @Override public Optional<V> get(K key) {
        return Optional.ofNullable(fwd.get(key));
    }

    public static <K, V> HashFunction<K, V> create() {
        return new HashFunction<>(new HashMap<>(), HashMultimap.create());
    }

    @Override public String toString() {
        return fwd.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bwd == null) ? 0 : bwd.hashCode());
        result = prime * result + ((fwd == null) ? 0 : fwd.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HashFunction other = (HashFunction) obj;
        if (bwd == null) {
            if (other.bwd != null)
                return false;
        } else if (!bwd.equals(other.bwd))
            return false;
        if (fwd == null) {
            if (other.fwd != null)
                return false;
        } else if (!fwd.equals(other.fwd))
            return false;
        return true;
    }

}