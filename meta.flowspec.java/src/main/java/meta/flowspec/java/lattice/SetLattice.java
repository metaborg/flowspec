package meta.flowspec.java.lattice;

import io.usethesource.capsule.Set;

public class SetLattice<E> implements CompleteLattice<Set.Immutable<E>> {
    private final Set.Immutable<E> top;

    /**
     * @param set The set to consider the top of this lattice
     */
    public SetLattice(Set.Immutable<E> set) {
        this.top = set;
    }

    @Override
    public Set.Immutable<E> top() {
        return top;
    }

    @Override
    public Set.Immutable<E> bottom() {
        return Set.Immutable.of();
    }

    @Override
    public boolean lte(Set.Immutable<E> one, Set.Immutable<E> other) {
        return other.containsAll(one); // one isSubSet.ImmutableOf other
    }

    @Override
    public Set.Immutable<E> glb(Set.Immutable<E> one, Set.Immutable<E> other) {
        return Set.Immutable.subtract(one, Set.Immutable.subtract(one, other));
    }

    @Override
    public Set.Immutable<E> lub(Set.Immutable<E> one, Set.Immutable<E> other) {
        return Set.Immutable.union(one, other);
    }
}
