package mb.flowspec.primitives;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.constraints.controlflow.ControlFlowConstraints;
import mb.nabl2.constraints.controlflow.IControlFlowConstraint;
import mb.nabl2.solver.ISolution;
import mb.nabl2.solver.SolverCore;
import mb.nabl2.solver.components.ControlFlowComponent;
import mb.nabl2.spoofax.analysis.IScopeGraphUnit;
import mb.nabl2.spoofax.primitives.AnalysisPrimitive;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.PersistentUnifier;

public class FS_build_cfg extends AnalysisPrimitive {

    public FS_build_cfg() {
        super(FS_build_cfg.class.getSimpleName());
    }

    @Override
    protected Optional<? extends ITerm> call(IScopeGraphUnit unit, ITerm term,
            List<ITerm> terms) throws InterpreterException {
        if(unit.solution().isPresent()) {
            return call(unit, term).map(B::newBlob);
        } else {
            return Optional.empty();
        }
    }

    private Optional<IScopeGraphUnit> call(IScopeGraphUnit unit, ITerm term) {
        return M.listElems(ControlFlowConstraints.matcher(), (l, constraints) -> buildCfg(unit, constraints)).match(term);
    }

    private IScopeGraphUnit buildCfg(IScopeGraphUnit unit, List<IControlFlowConstraint> constraints) {
        ISolution solution = unit.solution().get();
        SolverCore core = new SolverCore(null, new Ref<>(PersistentUnifier.Immutable.of()), null);
        ControlFlowComponent cfc = new ControlFlowComponent(core, solution.flowSpecSolution());
        for(IControlFlowConstraint flowConstraint : constraints) {
            cfc.solve(flowConstraint);
        }
        solution = solution.withFlowSpecSolution(cfc.finish());
        return unit.withSolution(solution);
    }
}
