package meta.flowspec.java;

import java.util.Set;

public interface Lattice<Val> {
    public Val top();

    public Val bottom();

    public boolean lte(Val one, Val other);

    public Val glb(Val one, Val other);

    public Val lub(Val one, Val other);

    default public Val glb(Set<Val> elements) {
        return elements.stream().reduce(top(), this::glb);
    }

    default public Val lub(Set<Val> elements) {
        return elements.stream().reduce(bottom(), this::lub);
    }

    default public Lattice<Val> flipLattice() {
        return new Lattice<Val>() {
            @Override
            public Val top() {
                return Lattice.this.bottom();
            }

            @Override
            public Val bottom() {
                return Lattice.this.top();
            }

            @Override
            public boolean lte(Val one, Val other) {
                return Lattice.this.gte(one, other);
            }

            @Override
            public Val glb(Val one, Val other) {
                return Lattice.this.lub(one, other);
            }

            @Override
            public Val lub(Val one, Val other) {
                return Lattice.this.glb(one, other);
            }
        };
    }

    default public boolean gte(Val one, Val other) {
        return lte(other, one);
    }
}
