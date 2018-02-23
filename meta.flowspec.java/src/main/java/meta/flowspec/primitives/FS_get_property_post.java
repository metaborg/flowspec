package meta.flowspec.primitives;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.spoofax.analysis.IScopeGraphUnit;
import org.metaborg.meta.nabl2.spoofax.primitives.AnalysisPrimitive;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.unification.PersistentUnifier;
import org.metaborg.meta.nabl2.util.ImmutableTuple2;
import org.spoofax.interpreter.core.InterpreterException;

public class FS_get_property_post extends AnalysisPrimitive {

    public FS_get_property_post() {
        super(FS_get_property_post.class.getSimpleName(), 1);
    }

    @Override public Optional<? extends ITerm> call(IScopeGraphUnit unit, ITerm term, List<ITerm> terms)
            throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: key");
        }
        final Optional<String> key = M.stringValue().match(terms.get(0), PersistentUnifier.Immutable.of());
        return key.<ITerm>flatMap(k -> TermIndex.get(term).<ITerm>flatMap(index -> {
            return unit.solution().<ITerm>flatMap(s -> {
                return Optional.ofNullable(s.flowSpecSolution().postProperties().get(ImmutableTuple2.of(CFGNode.normal(index), k)));
            });
        }));
    }

}