package mb.flowspec.runtime.interpreter.values;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import io.usethesource.capsule.Map.Immutable;
import io.usethesource.capsule.Map.Transient;
import io.usethesource.capsule.util.EqualityComparator;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;

public class Map<K extends ITerm, V extends ITerm> implements IMap<K, V> {
    private final Immutable<K, V> map;
    public final V topValue;
    private final ImmutableClassToInstanceMap<Object> attachments;

    public Map(Immutable<K, V> map) {
        this(map, null, ImmutableClassToInstanceMap.builder().build());
    }

    public Map(Immutable<K, V> map, V topValue) {
        this(map, topValue, ImmutableClassToInstanceMap.builder().build());
    }

    public Map(Immutable<K, V> map, V topValue, ImmutableClassToInstanceMap<Object> attachments) {
        this.map = map;
        this.topValue = topValue;
        this.attachments = attachments;
    }

    public Map<K, V> update(Immutable<K, V> map) {
        return new Map<>(map, topValue, attachments);
    }

    @Override
    public Immutable<K, V> getMap() {
        return this.map;
    }

    public static <K extends ITerm, V extends ITerm> Map<K, V> of(V topValue) {
        return new Map<>(Immutable.of(), topValue, ImmutableClassToInstanceMap.builder().build());
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

    @Deprecated
    public boolean containsKeyEquivalent(Object o, EqualityComparator<Object> cmp) {
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

    @Deprecated
    public boolean containsValueEquivalent(Object o, EqualityComparator<Object> cmp) {
        return map.containsValueEquivalent(o, cmp);
    }

    public Iterator<Entry<K, V>> entryIterator() {
        return map.entryIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof IMap))
            return false;
        @SuppressWarnings("rawtypes")
        IMap other = (IMap) obj;
        if(getMap() == null) {
            if(other.getMap() != null)
                return false;
        } else if(!getMap().equals(other.getMap()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Deprecated
    public V getEquivalent(Object o, EqualityComparator<Object> cmp) {
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

    @Deprecated
    public boolean equivalent(Object o, EqualityComparator<Object> cmp) {
        return map.equivalent(o, cmp);
    }

    public boolean isTransientSupported() {
        return map.isTransientSupported();
    }

    public Transient<K, V> asTransient() {
        return map.asTransient();
    }

    @Deprecated
    public Map<K, V> __putEquivalent(K key, V val, EqualityComparator<Object> cmp) {
        return update(map.__putEquivalent(key, val, cmp));
    }

    @Deprecated
    public Map<K, V> __removeEquivalent(K key, EqualityComparator<Object> cmp) {
        return update(map.__removeEquivalent(key, cmp));
    }

    @Deprecated
    public Map<K, V> __putAllEquivalent(java.util.Map<? extends K, ? extends V> map, EqualityComparator<Object> cmp) {
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

    @Override
    public boolean isGround() {
        return true;
    }

    @Override
    public Multiset<ITermVar> getVars() {
        return ImmutableMultiset.of();
    }

    @Override
    public ImmutableClassToInstanceMap<Object> getAttachments() {
        return this.attachments;
    }

    @Override
    public <T> T match(Cases<T> cases) {
        return cases.caseAppl(this);
    }

    @Override
    public <T, E extends Throwable> T matchOrThrow(CheckedCases<T, E> cases) throws E {
        return cases.caseAppl(this);
    }

    @Override
    public String getOp() {
        return "Map";
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public List<ITerm> getArgs() {
        return new ImmutableList.Builder<ITerm>()
                .add(B.newList(
                        this.map.entrySet()
                            .stream()
                            .map(e -> B.newTuple(e.getKey(), e.getValue()))
                            .toArray(ITerm[]::new)))
                .build();
    }

    @Override
    public IMap<K, V> withAttachments(ImmutableClassToInstanceMap<Object> value) {
        return new Map<>(this.map, this.topValue, value);
    }
}