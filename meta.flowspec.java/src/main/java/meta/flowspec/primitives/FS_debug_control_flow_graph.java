package meta.flowspec.primitives;

import java.util.Optional;

import org.metaborg.meta.nabl2.controlflow.terms.ControlFlowGraphTerms;
import org.metaborg.meta.nabl2.spoofax.TermSimplifier;
import org.metaborg.meta.nabl2.spoofax.analysis.IScopeGraphUnit;
import org.metaborg.meta.nabl2.spoofax.primitives.AnalysisNoTermPrimitive;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.spoofax.interpreter.core.InterpreterException;

public class FS_debug_control_flow_graph extends AnalysisNoTermPrimitive {

    public FS_debug_control_flow_graph() {
        super(FS_debug_control_flow_graph.class.getSimpleName());
    }

    @Override public Optional<? extends ITerm> call(IScopeGraphUnit unit) throws InterpreterException {
        return unit.solution().filter(sol -> unit.isPrimary()).map(sol -> {
            return TermSimplifier.focus(unit.resource(),
                    ControlFlowGraphTerms.build(sol.flowSpecSolution()));
        });
    }

}