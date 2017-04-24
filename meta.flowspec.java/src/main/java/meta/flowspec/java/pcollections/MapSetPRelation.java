package meta.flowspec.java.pcollections;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.pcollections.Empty;
import org.pcollections.PMap;
import org.pcollections.PSet;

public class MapSetPRelation<L, R> implements PRelation<L, R> {
    protected final PMap<L, PSet<R>> map;
    
    public MapSetPRelation() {
        this.map = Empty.map();
    }

    protected MapSetPRelation(PMap<L, PSet<R>> map) {
        this.map = map;
    }

    @Override
    public PRelation<L, R> plus(L lhs, R rhs) {
        return new MapSetPRelation<>(this.map.plus(lhs, this.getRhsSet(lhs).plus(rhs)));
    }

    @Override
    public PRelation<L, R> plusAll(PRelation<? extends L, ? extends R> map) {
        PRelation<L, R> result = this;
        for (Map.Entry<? extends L, ? extends R> e : map.entrySet()) {
            result = result.plus(e.getKey(), e.getValue());
        }
        return result;
    }

    @Override
    public PRelation<L, R> plusAll(Map<? extends L, ? extends R> map) {
        PRelation<L, R> result = this;
        for (Map.Entry<? extends L, ? extends R> e : map.entrySet()) {
            result = result.plus(e.getKey(), e.getValue());
        }
        return result;
    }

    @Override
    public PRelation<L, R> minus(L lhs, R rhs) {
        PSet<R> newRhsSet = this.getRhsSet(lhs).minus(rhs);
        if (newRhsSet.isEmpty()) {
            return new MapSetPRelation<>(this.map.minus(lhs));
        }
        return new MapSetPRelation<>(this.map.plus(lhs, newRhsSet));
    }

    @Override
    public PRelation<L, R> minusAll(PRelation<? extends L, ? extends R> map) {
        PRelation<L, R> result = this;
        for (Map.Entry<? extends L, ? extends R> e : map.entrySet()) {
            result = result.minus(e.getKey(), e.getValue());
        }
        return result;
    }

    @Override
    public PRelation<L, R> minusAll(Map<? extends L, ? extends R> map) {
        PRelation<L, R> result = this;
        for (Map.Entry<? extends L, ? extends R> e : map.entrySet()) {
            result = result.minus(e.getKey(), e.getValue());
        }
        return result;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(L lhs, R rhs) {
        return this.getRhsSet(lhs).contains(rhs);
    }

    @Override
    public boolean containsLhs(L lhs) {
        return this.map.containsKey(lhs);
    }

    @Override
    public boolean containsRhs(R rhs) {
        return this.rhsSet().contains(rhs);
    }

    @Override
    public PSet<R> getRhsSet(L lhs) {
        PSet<R> rhs = this.map.get(lhs);
        if (rhs == null) {
            rhs = Empty.set();
        }
        return rhs;
    }

    @Override
    public Set<L> lhsSet() {
        return this.map.keySet();
    }

    private PSet<R> cachedRhsSet = null;

    @Override
    public PSet<R> rhsSet() {
        if (cachedRhsSet == null) {
            cachedRhsSet = this.map.entrySet().stream().map(e -> (PSet<R>) e.getValue()).reduce(Empty.set(),
                    (set1, set2) -> set1.plusAll(set2));
        }
        return cachedRhsSet;
    }

    private Set<Map.Entry<L, R>> cachedEntrySet = null;

    @Override
    public Set<Map.Entry<L, R>> entrySet() {
        if (cachedEntrySet == null) {
            cachedEntrySet = this.map.entrySet().stream()
                    .flatMap(e -> e.getValue().stream().map(r -> new Entry<>(e.getKey(), r)))
                    .collect(Collectors.toSet());
        }
        return cachedEntrySet;
    }

    public static class Entry<L, R> implements Map.Entry<L, R> {
        private L lhs;
        private R rhs;

        public Entry(L lhs, R rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public L getKey() {
            return this.lhs;
        }

        @Override
        public R getValue() {
            return this.rhs;
        }

        @Override
        public R setValue(R value) {
            throw new UnsupportedOperationException();
        }
    }
}
