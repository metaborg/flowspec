package mb.flowspec.primitives;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.controlflow.terms.ControlFlowGraphTerms;
import mb.nabl2.spoofax.analysis.IScopeGraphUnit;
import mb.nabl2.spoofax.primitives.AnalysisNoTermPrimitive;
import mb.nabl2.terms.ITerm;

public class FS_show_control_flow_graph extends AnalysisNoTermPrimitive {

    public FS_show_control_flow_graph() {
        super(FS_show_control_flow_graph.class.getSimpleName());
    }

    @Override public Optional<? extends ITerm> call(IScopeGraphUnit unit) throws InterpreterException {
        return unit.solution().filter(sol -> unit.isPrimary()).map(sol -> {
            return B.newString(ControlFlowGraphTerms.toDot(sol.flowSpecSolution()));
        });
    }

}
