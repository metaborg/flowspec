package mb.flowspec.primitives;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.solver.ISolution;
import mb.nabl2.spoofax.primitives.AnalysisPrimitive;
import mb.nabl2.terms.ITerm;

public class FS_get_cfg_succ extends AnalysisPrimitive {

    public FS_get_cfg_succ() {
        super(FS_get_cfg_succ.class.getSimpleName(), 1);
    }

    @Override
    public Optional<? extends ITerm> call(ISolution solution, ITerm term, List<ITerm> terms)
            throws InterpreterException {
        Optional<CFGNode> node = CFGNode.matcher().match(term);
        return node.<ITerm>map(n -> B.newList(solution.flowSpecSolution().controlFlowGraph().edges().get(n)));
    }

}
