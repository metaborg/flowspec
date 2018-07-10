package mb.flowspec.primitives;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.solver.ISolution;
import mb.nabl2.spoofax.primitives.AnalysisPrimitive;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.PersistentUnifier;
import mb.nabl2.util.ImmutableTuple2;

public class FS_get_property_post extends AnalysisPrimitive {

    public FS_get_property_post() {
        super(FS_get_property_post.class.getSimpleName(), 1);
    }

    @Override public Optional<? extends ITerm> call(ISolution solution, ITerm term, List<ITerm> terms)
            throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: key");
        }
        final Optional<String> key = M.stringValue().match(terms.get(0), PersistentUnifier.Immutable.of());
        return key.<ITerm>flatMap(k -> TermIndex.get(term).<ITerm>flatMap(index -> {
            return Optional.ofNullable(solution.flowSpecSolution().postProperties().get(ImmutableTuple2.of(CFGNode.normal(index), k)));
        }));
    }

}