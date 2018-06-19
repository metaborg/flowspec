package mb.flowspec.runtime.lattice;

import java.util.Optional;

public interface Lattice<E> {
    @SuppressWarnings("rawtypes")
    public static Lattice withoutOrder = new Lattice() {
        @Override
        public Optional partial_glb(Object one, Object other) {
            if (one.equals(other)) {
                return Optional.of(one);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public Optional partial_lub(Object one, Object other) {
            if (one.equals(other)) {
                return Optional.of(one);
            } else {
                return Optional.empty();
            }
        }
    };
    
    public Optional<E> partial_glb(E one, E other);

    public Optional<E> partial_lub(E one, E other);

    default public boolean leq(E one, E other) {
        return partial_lub(one, other).equals(Optional.of(other));
    }

    default public boolean geq(E one, E other) {
        return leq(other, one);
    }
}
