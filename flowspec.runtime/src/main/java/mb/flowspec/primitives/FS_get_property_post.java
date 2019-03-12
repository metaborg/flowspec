package mb.flowspec.primitives;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.terms.TermIndexed;

public class FS_get_property_post extends AnalysisPrimitive implements IGetPropertyPrimitive {

    public FS_get_property_post() {
        super(FS_get_property_post.class.getSimpleName(), 1);
    }

    @Override public Optional<? extends IStrategoTerm> call(IFlowSpecSolution solution, IStrategoTerm term,
        List<IStrategoTerm> terms) throws InterpreterException {
        return getProperty(term, terms, solution.postProperties()).map(t -> TermIndexed.addTermIndexToAnnos(getFactory(), t));
    }

}