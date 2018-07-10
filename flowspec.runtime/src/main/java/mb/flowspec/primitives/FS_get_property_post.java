package mb.flowspec.primitives;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.solver.ISolution;
import mb.nabl2.spoofax.primitives.AnalysisPrimitive;
import mb.nabl2.terms.ITerm;

public class FS_get_property_post extends AnalysisPrimitive implements IGetPropertyPrimitive {

    public FS_get_property_post() {
        super(FS_get_property_post.class.getSimpleName(), 1);
    }

    @Override public Optional<? extends ITerm> call(ISolution solution, ITerm term, List<ITerm> terms)
            throws InterpreterException {
        return getProperty(term, terms, solution.flowSpecSolution().postProperties());
    }

}