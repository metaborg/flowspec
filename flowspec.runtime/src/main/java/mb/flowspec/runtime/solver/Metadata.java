package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.nabl2.util.Tuple2;

@Immutable
public abstract class Metadata<T extends IStrategoTerm> {
    public enum Direction {
        Forward,
        Backward
    }

    @Parameter public abstract Direction dir();
    @Parameter public abstract CompleteLattice<T> lattice();
    /// String is name of module where TF was defined
    @Parameter public abstract Map.Immutable<Tuple2<String, Integer>, TransferFunction> transferFunctions();

    public Metadata<?> addAll(Metadata<?> value) {
        final Map.Immutable<Tuple2<String, Integer>, TransferFunction> tfs = transferFunctions().__putAll(value.transferFunctions());
        return ImmutableMetadata.of(dir(), lattice(), tfs);
    }
}
