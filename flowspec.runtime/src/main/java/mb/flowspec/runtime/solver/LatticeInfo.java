package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.collect.ImmutableList;
import com.oracle.truffle.api.Truffle;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;

@Immutable
public abstract class LatticeInfo {
    @SuppressWarnings("rawtypes")
    @Parameter public abstract Map.Immutable<String, UserDefinedLattice> latticeDefs();

    @SuppressWarnings("rawtypes")
    public static IMatcher<LatticeInfo> match() {
        return M.listElems(tupleMatcher(), (list, tuples) -> {
            Map.Transient<String, UserDefinedLattice> latticeDefs = Map.Transient.of();
            for (ImmutableTuple2<String, UserDefinedLattice> t2 : tuples) {
                latticeDefs.__put(t2._1(), t2._2());
            }
            return ImmutableLatticeInfo.of(latticeDefs.freeze());
        });
    }

    @SuppressWarnings("rawtypes")
    protected static IMatcher<ImmutableTuple2<String, UserDefinedLattice>> tupleMatcher() {
        return M.<String, ImmutableList<String>, ITerm, Function, Function, ImmutableTuple2<String, UserDefinedLattice>>tuple5(
                M.stringValue(), 
                M.listElems(M.stringValue()), 
                M.term(),
                Function.matchLUB(),
                Function.matchNullary(),
                (appl, name, vars, type, lub, top) -> {
                    Object top_object = Truffle.getRuntime().createCallTarget(top).call(new Object[0]);
                    return ImmutableTuple2.<String, UserDefinedLattice>of(name, new UserDefinedLattice<>(top_object, lub));
                });
    }

    public LatticeInfo addAll(LatticeInfo lattices) {
        return ImmutableLatticeInfo.of(latticeDefs().__putAll(lattices.latticeDefs()));
    }
    
    public static LatticeInfo of() {
        return ImmutableLatticeInfo.of(Map.Immutable.of());
    }
}
