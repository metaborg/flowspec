package mb.flowspec.runtime.lattice;

import java.util.Map.Entry;

import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map.Transient;
import mb.flowspec.runtime.interpreter.values.Map;

public class MapLattice<K extends IStrategoTerm, V extends IStrategoTerm> implements CompleteLattice<Map<K, V>> {
    public final CompleteLattice<V> valueLattice;
    
    public MapLattice(CompleteLattice<V> valueLattice) {
        this.valueLattice = valueLattice;
    }

    @Override
    public Map<K, V> top() {
        return Map.of(valueLattice.top());
    }

    @Override
    public Map<K, V> bottom() {
        return Map.of(valueLattice.top());
    }

    @Override
    public Map<K, V> glb(Map<K, V> one, Map<K, V> other) {
        Transient<K, V> result = Transient.of();
        for(Entry<K, V> kv : one.entrySet()) {
            K key = kv.getKey();
            V oneValue = kv.getValue();
            V otherValue = other.get(key);
            result.__put(key, valueLattice.glb(oneValue, otherValue));
        }
        return one.update(result.freeze());
    }

    @Override
    public Map<K, V> lub(Map<K, V> one, Map<K, V> other) {
        Transient<K, V> result = Transient.of();
        for(Entry<K, V> kv : one.entrySet()) {
            K key = kv.getKey();
            V oneValue = kv.getValue();
            V otherValue = other.get(key);
            result.__put(key, valueLattice.lub(oneValue, otherValue));
        }
        return one.update(result.freeze());
    }
}
