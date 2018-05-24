package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.rmi.activation.UnknownObjectException;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.oracle.truffle.api.Truffle;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;

@Immutable
public abstract class LatticeInfo {
    @SuppressWarnings("rawtypes")
    @Parameter public abstract Map.Immutable<String, CompleteLattice> latticeDefs();

    @SuppressWarnings("rawtypes")
    public static IMatcher<LatticeInfo> match() {
        return M.listElems(tupleMatcher(), (list, tuples) -> {
            Map.Transient<String, CompleteLattice> latticeDefs = Map.Transient.of();
            for (ImmutableTuple2<String, CompleteLattice> t2 : tuples) {
                latticeDefs.__put(t2._1(), t2._2());
            }
            return ImmutableLatticeInfo.of(latticeDefs.freeze());
        });
    }

    @SuppressWarnings("rawtypes")
    protected static IMatcher<ImmutableTuple2<String, CompleteLattice>> tupleMatcher() {
        return M.tuple5(
                M.stringValue(), 
                M.listElems(M.stringValue()), 
                M.term(),
                Function.matchLUB(),
                Function.matchNullary(),
                (appl, name, vars, type, lub, top) -> {
                    Object top_object = Truffle.getRuntime().createCallTarget(top).call(new Object[0]);
                    return ImmutableTuple2.of(name, new CompleteLattice() {
                        public Object top() {
                            return top_object;
                        }

                        public Object bottom() {
                            throw new RuntimeException(new UnknownObjectException("bottom not read from FlowSpec definition"));
                        }

                        public Object glb(Object one, Object other) {
                            throw new RuntimeException(new UnknownObjectException("glb not read from FlowSpec definition"));
                        }

                        public Object lub(Object one, Object other) {
                            return Truffle.getRuntime().createCallTarget(lub)
                                    .call(new Object[] {one, other});
                        }
                    });
                });
    }

}
