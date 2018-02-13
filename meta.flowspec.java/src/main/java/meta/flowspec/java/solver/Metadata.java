package meta.flowspec.java.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.java.lattice.CompleteLattice;

@Immutable
public abstract class Metadata {
    public enum Direction {
        Forward,
        Backward;

        public static IMatcher<Direction> match() {
            return M.cases(
                M.appl0("Bw", appl -> Backward),
                M.appl0("Fw", appl -> Forward)
            );
        }
    }

    @Parameter public abstract Direction dir();
    @Parameter public abstract CompleteLattice<Object> lattice();
    @Parameter public abstract Type type();
    @Parameter public abstract TransferFunction[] transferFunctions();
}
