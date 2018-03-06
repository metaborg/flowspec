package mb.flowspec.runtime.solver;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import java.util.Map.Entry;
import java.util.Optional;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.metaborg.meta.nabl2.terms.IStringTerm;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;
import org.metaborg.meta.nabl2.util.ImmutableTuple2;
import org.metaborg.meta.nabl2.util.ImmutableTuple3;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.flowspec.runtime.lattice.FullSetLattice;
import mb.flowspec.runtime.solver.Metadata.Direction;
import mb.flowspec.runtime.solver.ImmutableMetadata;
import mb.flowspec.runtime.solver.ImmutableTFFileInfo;

@Immutable
public abstract class TFFileInfo {
    @Parameter public abstract BinaryRelation.Immutable<String, String> dependsOn();
    @Parameter public abstract Map.Immutable<String, Metadata<?>> metadata();

    public TFFileInfo addAll(TFFileInfo other) {
        Map.Transient<String, Metadata<?>> propMetadata = this.metadata().asTransient();
        BinaryRelation.Transient<String, String> dependsOn = this.dependsOn().asTransient();
        propMetadata.__putAll(other.metadata());
        for (Entry<String, String> e : other.dependsOn().entrySet()) {
            dependsOn.__insert(e.getKey(), e.getValue());
        }
        return ImmutableTFFileInfo.of(dependsOn.freeze(), propMetadata.freeze());
    }
    
    public void init(InitValues initValues) {
        for (Entry<String, Metadata<?>> e : metadata().entrySet()) {
            for (TransferFunction tf : e.getValue().transferFunctions()) {
                tf.init(initValues);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static IMatcher<TFFileInfo> match() {
        return M.listElems(tupleMatcher(), (list, t) -> {
            Map.Transient<String, Metadata<?>> propMetadata = Map.Transient.of();
            BinaryRelation.Transient<String, String> dependsOn = BinaryRelation.Transient.of();
            for (ImmutableTuple3<String, Direction, TransferFunction[]> t3 : t) {
                String propName = t3._1();
                Direction dir = t3._2();
                TransferFunction[] tfs = t3._3();

                CompleteLattice lattice = (CompleteLattice) new FullSetLattice<IStringTerm>();
                switch (propName) {
                    case "veryBusy":
                    case "available":
                        propMetadata.__put(propName, ImmutableMetadata.of(dir, lattice.flip(), tfs));
                        break;
                    default:
                        propMetadata.__put(propName, ImmutableMetadata.of(dir, lattice, tfs));
                }
            }
            return ImmutableTFFileInfo.of(dependsOn.freeze(), propMetadata.freeze());
        });
    }

    protected static IMatcher<ImmutableTuple3<String, Direction, TransferFunction[]>> tupleMatcher() {
        return (term, unifier) -> 
            Optional.of(
                M.tuple2(
                        M.string(), 
                        M.tuple2(
                                Direction.match(), 
                                TransferFunction.matchList(), 
                                (appl, dir, tfs) -> ImmutableTuple2.of(dir, tfs)), 
                        (appl, string, t2) -> {
                            String propName = string.getValue();
                            Direction dir = t2._1();
                            TransferFunction[] tfs = t2._2();
                            return ImmutableTuple3.of(propName,  dir, tfs);
                        })
                    .match(term, unifier)
                    .orElseThrow(() -> 
                            new ParseException("Parse error on reading the transfer function tuple")));
    }
    
    public static TFFileInfo of() {
        return ImmutableTFFileInfo.of(BinaryRelation.Immutable.of(), Map.Immutable.of());
    }
}
