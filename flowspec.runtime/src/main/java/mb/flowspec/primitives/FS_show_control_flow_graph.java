package mb.flowspec.primitives;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.ControlFlowGraphTerms;
import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.terms.B;
import mb.nabl2.spoofax.analysis.IResult;

public class FS_show_control_flow_graph extends AnalysisPrimitive {

    public FS_show_control_flow_graph() {
        super(FS_show_control_flow_graph.class.getSimpleName(), 0);
    }

    @Override protected Optional<? extends IStrategoTerm> call(IResult result, IStrategoTerm term,
        List<IStrategoTerm> terms) throws InterpreterException {
        if(result.partial()) {
            return Optional.empty();
        }
        final Optional<IFlowSpecSolution> solution = getFSSolution(result);
        return Optional.of(B.string(ControlFlowGraphTerms.toDot(solution.get())));
    }

}
