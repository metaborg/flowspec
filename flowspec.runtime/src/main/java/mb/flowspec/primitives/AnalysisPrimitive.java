package mb.flowspec.primitives;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.nabl2.solver.ISolution;
import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.stratego.StrategoBlob;

public abstract class AnalysisPrimitive extends AbstractPrimitive {
    final protected int tvars;

    public AnalysisPrimitive(String name, int tvars) {
        super(name, 0, tvars + 1); // + 1 for the analysisTerm argument
        this.tvars = tvars;
    }

    @Override public final boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
        throws InterpreterException {
        final List<IStrategoTerm> termArgs = Arrays.asList(Arrays.copyOfRange(tvars, 1, tvars.length));
        return call(env.current(), tvars[0], termArgs, env.getFactory()).map(t -> {
            env.setCurrent(t);
            return true;
        }).orElse(false);
    }

    private Optional<? extends IStrategoTerm> call(IStrategoTerm term, IStrategoTerm analysisTerm, List<IStrategoTerm> otherTerms,
        ITermFactory factory) throws InterpreterException {
        if(otherTerms.size() != tvars) {
            throw new InterpreterException("Expected " + tvars + " term arguments, but got " + otherTerms.size());
        }
        if(analysisTerm instanceof StrategoBlob) {
            StrategoBlob blob = (StrategoBlob) analysisTerm;
            if(blob.value() instanceof IResult) {
                IResult result = (IResult) blob.value();
                return call(result, term, otherTerms);
            }
        }
        throw new IllegalArgumentException("Not a valid analysis term.");
    }

    protected Optional<? extends IStrategoTerm> call(IResult result, IStrategoTerm term, List<IStrategoTerm> terms)
        throws InterpreterException {
        Optional<IFlowSpecSolution> solution = getFSSolution(result);
        if(solution.isPresent()) {
            return call(solution.get(), term, terms);
        }
        return Optional.empty();
    }

    protected Optional<? extends IStrategoTerm> call(IFlowSpecSolution solution, IStrategoTerm term, List<IStrategoTerm> terms)
        throws InterpreterException {
        return Optional.empty();
    }

    public static Optional<IFlowSpecSolution> getFSSolution(IResult result) {
        ISolution solution = result.solution();
        if(solution instanceof IFlowSpecSolution) {
            return Optional.of((IFlowSpecSolution) solution);
        }
        return result.customAnalysis().flatMap(t -> {
            if(t instanceof IFlowSpecSolution) {
                return Optional.of((IFlowSpecSolution) t);
            }
            return Optional.empty();
        });
    }
}
