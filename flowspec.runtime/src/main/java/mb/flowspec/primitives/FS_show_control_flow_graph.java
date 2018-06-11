package mb.flowspec.primitives;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.controlflow.terms.ControlFlowGraphTerms;
import mb.nabl2.spoofax.analysis.IScopeGraphUnit;
import mb.nabl2.spoofax.primitives.AnalysisPrimitive;
import mb.nabl2.terms.ITerm;

public class FS_show_control_flow_graph extends AnalysisPrimitive {

    public FS_show_control_flow_graph() {
        super(FS_show_control_flow_graph.class.getSimpleName());
    }

    @Override protected Optional<? extends ITerm> call(IScopeGraphUnit unit, ITerm term, List<ITerm> terms)
            throws InterpreterException {
        return unit.solution().filter(sol -> unit.isPrimary()).map(sol -> {
            return B.newString(ControlFlowGraphTerms.toDot(sol.flowSpecSolution()));
        });
    }

}
