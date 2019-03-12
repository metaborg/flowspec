package mb.flowspec.runtime.solver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mb.flowspec.runtime.lattice.CompleteLattice;

@SuppressWarnings("rawtypes")
public class LatticeInfo {
    public final Map<String, CompleteLattice> latticeDefs;

    public LatticeInfo() {
        this(Collections.emptyMap());
    }

    public LatticeInfo(Map<String, CompleteLattice> latticeDefs) {
        this.latticeDefs = latticeDefs;
    }

    public LatticeInfo addAll(LatticeInfo lattices) {
        Map<String, CompleteLattice> m = new HashMap<>(this.latticeDefs);
        m.putAll(lattices.latticeDefs);
        return new LatticeInfo(Collections.unmodifiableMap(m));
    }
}
