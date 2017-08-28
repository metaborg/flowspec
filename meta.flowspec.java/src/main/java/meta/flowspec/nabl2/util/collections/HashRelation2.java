package meta.flowspec.nabl2.util.collections;

import java.io.Serializable;

import meta.flowspec.nabl2.util.collections.HashRelation2;
import meta.flowspec.nabl2.util.collections.IRelation2;
import meta.flowspec.nabl2.util.collections.ISet;
import meta.flowspec.nabl2.util.collections.WrappedSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class HashRelation2<K, V> implements IRelation2.Mutable<K, V>, Serializable {

    private static final long serialVersionUID = 42L;

    private final SetMultimap<K, V> fwd;
    private final SetMultimap<V, K> bwd;

    private HashRelation2(SetMultimap<K, V> fwd, SetMultimap<V, K> bwd) {
        this.fwd = fwd;
        this.bwd = bwd;
    }

    @Override public IRelation2.Mutable<V, K> inverse() {
        return new HashRelation2<>(bwd, fwd);
    }

    @Override public boolean put(K key, V value) {
        if(fwd.put(key, value)) {
            bwd.put(value, key);
            return true;
        }
        return false;
    }

    @Override public boolean remove(K key, V value) {
        if(fwd.remove(key, value)) {
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
        return fwd.containsEntry(key, value);
    }

    @Override public ISet<K> keySet() {
        return WrappedSet.of(fwd.keySet());
    }

    @Override public ISet<V> valueSet() {
        return WrappedSet.of(bwd.keySet());
    }

    @Override public ISet<V> get(K key) {
        return WrappedSet.of(fwd.get(key));
    }

    public static <K, V> HashRelation2<K, V> create() {
        return new HashRelation2<>(HashMultimap.create(), HashMultimap.create());
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
        HashRelation2 other = (HashRelation2) obj;
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