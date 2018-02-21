package meta.flowspec.primitives;

import java.util.List;
import java.util.Optional;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.ImmutableCFGNode;
import org.metaborg.meta.nabl2.spoofax.analysis.IScopeGraphUnit;
import org.metaborg.meta.nabl2.spoofax.primitives.AnalysisPrimitive;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;
import org.metaborg.meta.nabl2.terms.unification.PersistentUnifier;
import org.metaborg.meta.nabl2.util.ImmutableTuple2;
import org.spoofax.interpreter.core.InterpreterException;

public class FS_get_property_pre extends AnalysisPrimitive {

    public FS_get_property_pre() {
        super(FS_get_property_pre.class.getSimpleName(), 1);
    }

    @Override public Optional<? extends ITerm> call(IScopeGraphUnit unit, ITerm term, List<ITerm> terms)
            throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: key");
        }
        final Optional<String> key = M.stringValue().match(terms.get(0), PersistentUnifier.Immutable.of());
        return key.<ITerm>flatMap(k -> TermIndex.get(term).<ITerm>flatMap(index -> {
            return unit.solution().<ITerm>flatMap(s -> {
                return Optional.ofNullable(s.flowSpecSolution().preProperties().get(ImmutableTuple2.of(ImmutableCFGNode.of(index, null, ICFGNode.Kind.Normal), k)));
            });
        }));
    }

}