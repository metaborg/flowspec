package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.lattice.CompleteLattice;

@Immutable
@SuppressWarnings("rawtypes")
public abstract class LatticeInfo {
    @Parameter public abstract Map.Immutable<String, CompleteLattice> latticeDefs();

    public LatticeInfo addAll(LatticeInfo lattices) {
        return ImmutableLatticeInfo.of(latticeDefs().__putAll(lattices.latticeDefs()));
    }
    
    public static LatticeInfo of() {
        return ImmutableLatticeInfo.of(Map.Immutable.of());
    }
}
