package meta.flowspec.java.lattice;

import java.util.Collection;
import java.util.Iterator;

import org.metaborg.meta.nabl2.terms.ITerm;

import io.usethesource.capsule.util.EqualityComparator;
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
    public static final Set TOP = new Set(new io.usethesource.capsule.Set.Immutable() {

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return true;
        }

        @Override
        public Iterator<Object> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray(Object[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection c) {
            return true;
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

		@Override
		public Object get(Object o) {
            throw new UnsupportedOperationException();
		}

		@Override
		public Iterator keyIterator() {
            throw new UnsupportedOperationException();
		}

		@Override
		public boolean equivalent(Object o, EqualityComparator cmp) {
            throw new UnsupportedOperationException();
		}

		@Override
		public io.usethesource.capsule.Set.Immutable __insert(Object key) {
			return this;
		}

		@Override
		public io.usethesource.capsule.Set.Immutable __remove(Object key) {
            throw new UnsupportedOperationException();
		}

		@Override
		public io.usethesource.capsule.Set.Immutable __insertAll(java.util.Set set) {
			return this;
		}

		@Override
		public io.usethesource.capsule.Set.Immutable __removeAll(java.util.Set set) {
            throw new UnsupportedOperationException();
		}

		@Override
		public io.usethesource.capsule.Set.Immutable __retainAll(java.util.Set set) {
            throw new UnsupportedOperationException();
		}

		@Override
		public boolean isTransientSupported() {
			return false;
		}

		@Override
		public io.usethesource.capsule.Set.Transient asTransient() {
			return null;
		}

    });
}
