package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Optional;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.InitFunction;
import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.Tuple2;

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
    @Parameter public abstract Optional<InitFunction> initFunction();
    /// String is name of module where TF was defined
    @Parameter public abstract Map.Immutable<Tuple2<String, Integer>, TransferFunction> transferFunctions();

    public Metadata<?> addAll(Metadata<?> value) {
        final Map.Immutable<Tuple2<String, Integer>, TransferFunction> tfs = transferFunctions().__putAll(value.transferFunctions());
        Optional<InitFunction> initF = initFunction();
        if(!initFunction().isPresent()) {
            initF = value.initFunction();
        }
        return ImmutableMetadata.of(dir(), lattice(), initF, tfs);
    }
}
