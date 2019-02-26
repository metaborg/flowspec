package mb.flowspec.runtime.interpreter.values;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import io.usethesource.capsule.Map.Immutable;
import io.usethesource.capsule.Map.Transient;
import io.usethesource.capsule.util.EqualityComparator;

public class Map<K extends IStrategoTerm, V extends IStrategoTerm> implements IMap<K, V>, Serializable {
    /**
     * Stratego compiler assumes constructors are maximally shared and does identity comparison.
     * So we initialize the constructors at runtime...
     */
    public static IStrategoConstructor cons = null;

    public static void initializeConstructor(ITermFactory tf) {
        cons = tf.makeConstructor(IMap.NAME, IMap.ARITY);
    }

    @Override public IStrategoConstructor getConstructor() {
        return cons != null ? cons : new StrategoConstructor(getName(), getSubtermCount());
    }

    private final Immutable<K, V> map;
    public final V topValue;

    public Map(Immutable<K, V> map) {
        this(map, null);
    }

    public Map(Immutable<K, V> map, V topValue) {
        this.map = map;
        this.topValue = topValue;
    }

    public Map<K, V> update(Immutable<K, V> map) {
        return new Map<>(map, topValue);
    }

    @Override public Immutable<K, V> getMap() {
        return this.map;
    }

    public static <K extends IStrategoTerm, V extends IStrategoTerm> Map<K, V> of(V topValue) {
        return new Map<>(Immutable.of(), topValue);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Deprecated public boolean containsKeyEquivalent(Object o, EqualityComparator<Object> cmp) {
        return map.containsKeyEquivalent(o, cmp);
    }

    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    public V get(Object o) {
        return map.getOrDefault(o, topValue);
    }

    public Iterator<K> keyIterator() {
        return map.keyIterator();
    }

    public Iterator<V> valueIterator() {
        return map.valueIterator();
    }

    @Deprecated public boolean containsValueEquivalent(Object o, EqualityComparator<Object> cmp) {
        return map.containsValueEquivalent(o, cmp);
    }

    public Iterator<Entry<K, V>> entryIterator() {
        return map.entryIterator();
    }

    @Override public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof IMap))
            return false;
        @SuppressWarnings("rawtypes") IMap other = (IMap) obj;
        if(getMap() == null) {
            if(other.getMap() != null)
                return false;
        } else if(!getMap().equals(other.getMap()))
            return false;
        return true;
    }

    @Override public int hashCode() {
        return map.hashCode();
    }

    @Override public String toString() {
        return map.toString();
    }

    @Deprecated public V getEquivalent(Object o, EqualityComparator<Object> cmp) {
        return map.getEquivalent(o, cmp);
    }

    public Map<K, V> __put(K key, V val) {
        return update(map.__put(key, val));
    }

    public Map<K, V> __remove(K key) {
        return update(map.__remove(key));
    }

    public Map<K, V> __putAll(java.util.Map<? extends K, ? extends V> map) {
        return update(this.map.__putAll(map));
    }

    @Deprecated public boolean equivalent(Object o, EqualityComparator<Object> cmp) {
        return map.equivalent(o, cmp);
    }

    public boolean isTransientSupported() {
        return map.isTransientSupported();
    }

    public Transient<K, V> asTransient() {
        return map.asTransient();
    }

    @Deprecated public Map<K, V> __putEquivalent(K key, V val, EqualityComparator<Object> cmp) {
        return update(map.__putEquivalent(key, val, cmp));
    }

    @Deprecated public Map<K, V> __removeEquivalent(K key, EqualityComparator<Object> cmp) {
        return update(map.__removeEquivalent(key, cmp));
    }

    @Deprecated public Map<K, V> __putAllEquivalent(java.util.Map<? extends K, ? extends V> map,
        EqualityComparator<Object> cmp) {
        return update(this.map.__putAllEquivalent(map, cmp));
    }

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V remove(Object key) {
        return map.remove(key);
    }

    public void putAll(java.util.Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        map.forEach(action);
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        map.replaceAll(function);
    }

    public V putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    public V replace(K key, V value) {
        return map.replace(key, value);
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }
}