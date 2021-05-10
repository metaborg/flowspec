package mb.flowspec.runtime.solver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.metaborg.util.tuple.Tuple2;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.lattice.CompleteLattice;

public class Metadata<T extends IStrategoTerm> {
    public enum Direction {
        Forward, Backward
    }

    public final Direction dir;
    public final CompleteLattice<T> lattice;
    /// String is name of module where TF was defined
    public final Map<Tuple2<String, Integer>, TransferFunction> transferFunctions;

    public Metadata(Direction dir, CompleteLattice<T> lattice,
        Map<Tuple2<String, Integer>, TransferFunction> transferFunctions) {
        this.dir = dir;
        this.lattice = lattice;
        this.transferFunctions = transferFunctions;
    }

    public Metadata<?> addAll(Metadata<?> value) {
        Map<Tuple2<String, Integer>, TransferFunction> m = new HashMap<>(this.transferFunctions);
        m.putAll(value.transferFunctions);
        return new Metadata<>(this.dir, this.lattice, Collections.unmodifiableMap(m));
    }
}
