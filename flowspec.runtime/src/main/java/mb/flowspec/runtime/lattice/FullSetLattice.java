package mb.flowspec.runtime.lattice;

import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Set.Immutable;
import mb.flowspec.runtime.interpreter.SymbolicLargestSetException;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Set;

public class FullSetLattice<E extends IStrategoTerm> implements CompleteLattice<ISet<E>> {
    @SuppressWarnings("unchecked")
    @Override
    public ISet<E> top() {
        return TOP;
    }

    @Override
    public ISet<E> bottom() {
        return new Set<>();
    }

    @Override
    public boolean leq(ISet<E> one, ISet<E> other) {
        if (other == TOP) {
            return true;
        } else if (one == TOP) {
            return false;
        } else {
            return other.getSet().containsAll(one.getSet());
        }
    }

    @Override
    public ISet<E> glb(ISet<E> one, ISet<E> other) {
        if (other == TOP) {
            return one;
        } else if (one == TOP) {
            return other;
        } else {
            return new Set<>(io.usethesource.capsule.Set.Immutable.subtract(one.getSet(),
                    io.usethesource.capsule.Set.Immutable.subtract(one.getSet(), other.getSet())));
        }
    }

    @Override
    public ISet<E> lub(ISet<E> one, ISet<E> other) {
        if (one == TOP || other == TOP) {
            return this.top();
        } else {
            return new Set<>(io.usethesource.capsule.Set.Immutable.union(one.getSet(), other.getSet()));
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public static final ISet TOP = new ISet() {
        @Override
        public Immutable getSet() {
            throw new SymbolicLargestSetException("Attempting to read symbolic set of all values");
        }
    };
}
