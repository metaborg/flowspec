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
import mb.nabl2.terms.Terms;
import mb.nabl2.terms.stratego.StrategoBlob;

public abstract class AnalysisPrimitive extends AbstractPrimitive {
    final protected int tvars;
    private ITermFactory tf;

    public AnalysisPrimitive(String name, int tvars) {
        super(name, 0, tvars + 1); // + 1 for the analysisTerm argument
        this.tvars = tvars;
    }

    @Override public final boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
        throws InterpreterException {
        tf = env.getFactory();
        final List<IStrategoTerm> termArgs = Arrays.asList(Arrays.copyOfRange(tvars, 1, tvars.length));
        return call(env.current(), tvars[0], termArgs, tf).map(t -> {
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
        throw new IllegalArgumentException("Cannot find FlowSpec solution in analysis term.");
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
        return result.customAnalysis().flatMap(customAnalysis -> {
            if(customAnalysis instanceof IFlowSpecSolution) {
                return Optional.of((IFlowSpecSolution) customAnalysis);
            } else {
                return customAnalysis.match(Terms.<Optional<IFlowSpecSolution>>cases()
                    .blob(blob -> {
                        Object blobValue = blob.getValue();
                        if(blobValue instanceof IFlowSpecSolution) {
                            return Optional.of((IFlowSpecSolution) blobValue);
                        } else {
                            if(blobValue instanceof IResult && blobValue != result) {
                                return getFSSolution((IResult) blobValue);
                            } else {
                                return Optional.empty();
                            }
                        }
                    })
                    .otherwise(t -> Optional.empty()));
            }
        });
    }
    
    public ITermFactory getFactory() {
        return tf;
    }
}
