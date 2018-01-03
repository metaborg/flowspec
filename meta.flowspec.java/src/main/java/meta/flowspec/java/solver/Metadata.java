package meta.flowspec.java.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import meta.flowspec.java.lattice.CompleteLattice;

@Immutable
public abstract class Metadata {
    public enum Direction {
        Forward,
        Backward,
        FlowInsensitive;
        
        public static IMatcher<Direction> match() {
            return M.cases(
                M.appl0("Bw", appl -> Backward),
                M.appl0("Fw", appl -> Forward),
                M.appl0("NA", appl -> FlowInsensitive)
            );
        }
    }

    @Parameter public abstract Direction dir();
    @Parameter public abstract CompleteLattice<Object> lattice();
    @Parameter public abstract Type type();
}
