package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Map.Entry;
import java.util.Optional;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.TransferFunction;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.flowspec.runtime.lattice.MapLattice;
import mb.flowspec.runtime.solver.Metadata.Direction;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.ImmutableTuple3;
import mb.nabl2.util.Tuple2;

@Immutable
public abstract class TransferFunctionInfo {
    @Parameter
    public abstract BinaryRelation.Immutable<String, String> dependsOn();

    @Parameter
    public abstract Map.Immutable<String, Metadata<?>> metadata();

    public TransferFunctionInfo addAll(TransferFunctionInfo other) {
        Map.Transient<String, Metadata<?>> propMetadata = this.metadata().asTransient();
        BinaryRelation.Transient<String, String> dependsOn = this.dependsOn().asTransient();
        for(Entry<String, Metadata<?>> e : other.metadata().entrySet()) {
            if(propMetadata.containsKey(e.getKey())) {
                propMetadata.__put(e.getKey(), propMetadata.get(e.getKey()).addAll(e.getValue()));
            } else {
                propMetadata.__put(e.getKey(), e.getValue());
            }
        }
        for (Entry<String, String> e : other.dependsOn().entrySet()) {
            dependsOn.__insert(e.getKey(), e.getValue());
        }
        return ImmutableTransferFunctionInfo.of(dependsOn.freeze(), propMetadata.freeze());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static IMatcher<TransferFunctionInfo> match(LatticeInfo latticeInfo, String moduleName) {
        Map.Transient<String, CompleteLattice> latticeDefs = latticeInfo.latticeDefs().asTransient();
        return M.listElems(tupleMatcher(), (list, tuples) -> {
            Map.Transient<String, Metadata<?>> propMetadata = Map.Transient.of();
            BinaryRelation.Transient<String, String> dependsOn = BinaryRelation.Transient.of();
            for (ImmutableTuple3<String, Direction, ImmutableTuple2<Type, TransferFunction[]>> t4 : tuples) {
                String propName = t4._1();
                Type type = t4._3()._1();
                Direction dir = t4._2();
                TransferFunction[] tfs = t4._3()._2();
                
                Map.Transient<Tuple2<String, Integer>, TransferFunction> tfMap = Map.Transient.of();
                for(int i = 0; i < tfs.length; i++) {
                    TransferFunction tf = tfs[i];
                    tfMap.__put(ImmutableTuple2.of(moduleName, i), tf);
                }

                propMetadata.__put(propName, ImmutableMetadata.of(dir, latticeFromType(latticeDefs, type), tfMap.freeze()));
            }
            return ImmutableTransferFunctionInfo.of(dependsOn.freeze(), propMetadata.freeze());
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static CompleteLattice latticeFromType(Map<String, CompleteLattice> latticeDefs, Type type) {
        // TODO: Replace Map with MayMap and MustMap
        if (type instanceof UserType) {
            UserType utype = (UserType) type;
            return latticeDefs.get(utype.name());
        } else if (type instanceof MapType) {
            MapType mtype = (MapType) type;
            return new MapLattice(latticeFromType(latticeDefs, mtype.value()));
        }
        return null;
    }

    protected static IMatcher<ImmutableTuple3<String, Direction, ImmutableTuple2<Type, TransferFunction[]>>> tupleMatcher() {
        return (term, unifier) -> 
            Optional.of(
                M.tuple2(M.stringValue(), M.tuple3(Type.matchType(), Direction.match(), TransferFunction.matchList(),
                    (appl, type, dir, tfs) -> ImmutableTuple3.of(type, dir, tfs)), (appl, propName, t2) -> {
                        Type type = t2._1();
                        Direction dir = t2._2();
                        TransferFunction[] tfs = t2._3();
                        return ImmutableTuple3.of(propName, dir, ImmutableTuple2.of(type, tfs));
                    })
                    .match(term, unifier)
                    .orElseThrow(() -> new ParseException("Parse error on reading the transfer function tuple")));
    }

    public static TransferFunctionInfo of() {
        return ImmutableTransferFunctionInfo.of(BinaryRelation.Immutable.of(), Map.Immutable.of());
    }
}
