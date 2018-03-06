package mb.flowspec.runtime.solver;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.lattice.CompleteLattice;

@Immutable
public abstract class Metadata<T extends ITerm> {
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
    @Parameter public abstract CompleteLattice<T> lattice();
    @Parameter public abstract TransferFunction[] transferFunctions();
}
