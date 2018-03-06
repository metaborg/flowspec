package meta.flowspec.java.lattice;

import org.metaborg.meta.nabl2.terms.ITerm;

import meta.flowspec.java.interpreter.values.Set;

public class FullSetLattice<E extends ITerm> implements CompleteLattice<Set<E>> {
    @SuppressWarnings("unchecked")
    @Override
    public Set<E> top() {
        return TOP;
    }

    @Override
    public Set<E> bottom() {
        return new Set<>();
    }

    @Override
    public boolean lte(Set<E> one, Set<E> other) {
        if (other == TOP) {
            return true;
        } else if (one == TOP) {
            return false;
        } else {
            return other.set.containsAll(one.set);
        }
    }

    @Override
    public Set<E> glb(Set<E> one, Set<E> other) {
        if (other == TOP) {
            return one;
        } else if (one == TOP) {
            return other;
        } else {
            return new Set<>(io.usethesource.capsule.Set.Immutable.subtract(one.set, io.usethesource.capsule.Set.Immutable.subtract(one.set, other.set)));
        }
    }

    @Override
    public Set<E> lub(Set<E> one, Set<E> other) {
        if (one == TOP || other == TOP) {
            return this.top();
        } else {
            return new Set<>(io.usethesource.capsule.Set.Immutable.union(one.set, other.set));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Set TOP = new Set(null);
}
