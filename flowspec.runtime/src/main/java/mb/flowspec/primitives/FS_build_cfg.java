package mb.flowspec.primitives;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;

import org.metaborg.util.Ref;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.constraints.controlflow.ControlFlowConstraints;
import mb.nabl2.constraints.controlflow.IControlFlowConstraint;
import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.controlflow.terms.ICompleteControlFlowGraph.Immutable;
import mb.nabl2.controlflow.terms.IFlowSpecSolution;
import mb.nabl2.solver.ISolution;
import mb.nabl2.solver.SolverCore;
import mb.nabl2.solver.components.ControlFlowComponent;
import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.spoofax.primitives.AnalysisPrimitive;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.PersistentUnifier;

public class FS_build_cfg extends AnalysisPrimitive {
    private static final ILogger logger = LoggerUtils.logger(FS_build_cfg.class);

    public FS_build_cfg() {
        super(FS_build_cfg.class.getSimpleName());
    }

    @Override
    protected Optional<? extends ITerm> call(IResult result, ITerm term,
            List<ITerm> terms) throws InterpreterException {
        if(!result.partial()) {
            return call(result, term).map(B::newBlob);
        } else {
            return Optional.empty();
        }
    }

    private Optional<IResult> call(IResult result, ITerm term) {
        return M.listElems(ControlFlowConstraints.matcher(), (l, constraints) -> buildCfg(result, constraints)).match(term);
    }

    private IResult buildCfg(IResult result, List<IControlFlowConstraint> constraints) {
        ISolution solution = result.solution();
        SolverCore core = new SolverCore(null, new Ref<>(PersistentUnifier.Immutable.of()), null);
        ControlFlowComponent cfc = new ControlFlowComponent(core, solution.flowSpecSolution());
        for(IControlFlowConstraint flowConstraint : constraints) {
            cfc.solve(flowConstraint);
        }
        IFlowSpecSolution<CFGNode> fsSolution = cfc.finish();

        Immutable<CFGNode> cfg = fsSolution.controlFlowGraph();
        logger.debug("CFG has {} nodes and {} edges", cfg.nodes().size(), cfg.edges().size());

        solution = solution.withFlowSpecSolution(fsSolution);
        return result.withSolution(solution);
    }
}
