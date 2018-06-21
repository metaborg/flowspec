package mb.flowspec.runtime.lattice;

import java.util.Optional;
import java.util.Set;

import mb.flowspec.runtime.interpreter.InitValues;

public interface CompleteLattice<E> extends Lattice<E> {
    public E top();

    public E bottom();

    public E glb(E one, E other);

    public E lub(E one, E other);
    
    default Optional<E> partial_glb(E one, E other) {
        return Optional.of(glb(one, other));
    }
    
    default Optional<E> partial_lub(E one, E other) {
        return Optional.of(lub(one, other));
    }

    default public E glb(Set<E> elements) {
        return elements.stream().reduce(top(), this::glb);
    }

    default public E lub(Set<E> elements) {
        return elements.stream().reduce(bottom(), this::lub);
    }

    default public boolean leq(E one, E other) {
        return lub(one, other).equals(other);
    }

    default public boolean nleq(E one, E other) {
        return !leq(one, other);
    }

    default public boolean geq(E one, E other) {
        return leq(other, one);
    }

    default public CompleteLattice<E> flip() {
        return new CompleteLattice<E>() {
            @Override
            public E top() {
                return CompleteLattice.this.bottom();
            }

            @Override
            public E bottom() {
                return CompleteLattice.this.top();
            }

            @Override
            public E glb(E one, E other) {
                return CompleteLattice.this.lub(one, other);
            }

            @Override
            public E lub(E one, E other) {
                return CompleteLattice.this.glb(one, other);
            }

            @Override
            public boolean leq(E one, E other) {
                return CompleteLattice.this.geq(one, other);
            }

            @Override
            public boolean geq(E one, E other) {
                return CompleteLattice.this.leq(other, one);
            }

            @Override
            public void init(InitValues initValues) {}
        };
    }

    public void init(InitValues initValues);
}
