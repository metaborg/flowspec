package meta.flowspec.java;

import java.util.Set;

public interface Lattice<E> {
    public E top();

    public E bottom();

    public boolean lte(E one, E other);

    public E glb(E one, E other);

    public E lub(E one, E other);

    default public E glb(Set<E> elements) {
        return elements.stream().reduce(top(), this::glb);
    }

    default public E lub(Set<E> elements) {
        return elements.stream().reduce(bottom(), this::lub);
    }

    default public Lattice<E> flipLattice() {
        return new Lattice<E>() {
            @Override
            public E top() {
                return Lattice.this.bottom();
            }

            @Override
            public E bottom() {
                return Lattice.this.top();
            }

            @Override
            public boolean lte(E one, E other) {
                return Lattice.this.gte(one, other);
            }

            @Override
            public E glb(E one, E other) {
                return Lattice.this.lub(one, other);
            }

            @Override
            public E lub(E one, E other) {
                return Lattice.this.glb(one, other);
            }
        };
    }

    default public boolean gte(E one, E other) {
        return lte(other, one);
    }
}
