package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Optional;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.collect.ImmutableList;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.flowspec.runtime.lattice.FullSetLattice;
import mb.flowspec.runtime.lattice.UserDefinedLattice;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;

@Immutable
@SuppressWarnings("rawtypes")
public abstract class LatticeInfo {
    @Parameter public abstract Map.Immutable<String, CompleteLattice> latticeDefs();

    public static IMatcher<LatticeInfo> match() {
        return M.listElems(tupleMatcher(), (list, tuples) -> {
            Map.Transient<String, CompleteLattice> latticeDefs = Map.Transient.of();
            for (ImmutableTuple2<String, UserDefinedLattice> t2 : tuples) {
                latticeDefs.__put(t2._1(), t2._2());
            }
            latticeDefs.__put("MaySet", new FullSetLattice());
            latticeDefs.__put("MustSet", new FullSetLattice().flip());
            return ImmutableLatticeInfo.of(latticeDefs.freeze());
        });
    }

    protected static IMatcher<ImmutableTuple2<String, UserDefinedLattice>> tupleMatcher() {
        return (term, unifier) -> 
                Optional.of(M.<String, ImmutableList<String>, ITerm, UserDefinedLattice, ImmutableTuple2<String, UserDefinedLattice>>tuple4(
                M.stringValue(), 
                M.listElems(M.stringValue()), 
                M.term(),
                M.tuple3(Function.matchLUB(), Function.matchNullary(), Function.matchNullary(),
                        (appl, lub, top, bottom) -> new UserDefinedLattice(top, bottom, lub)),
                (appl, name, vars, type, udl) -> {
                    return ImmutableTuple2.<String, UserDefinedLattice>of(name, udl);
                })
                .match(term, unifier)
                .orElseThrow(() -> new ParseException("Parse error on reading function")));
    }

    public LatticeInfo addAll(LatticeInfo lattices) {
        return ImmutableLatticeInfo.of(latticeDefs().__putAll(lattices.latticeDefs()));
    }
    
    public static LatticeInfo of() {
        return ImmutableLatticeInfo.of(Map.Immutable.of());
    }
}
