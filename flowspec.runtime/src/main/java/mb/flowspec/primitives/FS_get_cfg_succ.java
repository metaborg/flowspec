package mb.flowspec.primitives;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Set;
import mb.flowspec.controlflow.ControlFlowGraphBuilder;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.terms.B;
import mb.flowspec.terms.M;

public class FS_get_cfg_succ extends AnalysisPrimitive {

    public FS_get_cfg_succ() {
        super(FS_get_cfg_succ.class.getSimpleName(), 0);
    }

    @Override public Optional<? extends IStrategoTerm> call(IFlowSpecSolution solution, IStrategoTerm term,
        List<IStrategoTerm> terms) throws InterpreterException {
        return M.maybe(() -> {
            ICFGNode node = ControlFlowGraphBuilder.cfgNode(term);
            final Set.Immutable<ICFGNode> set = solution.controlFlowGraph().edges().get(node);
            final IStrategoTerm[] nodes = new IStrategoTerm[set.size()];
            int i = 0;
            for(ICFGNode cfgNode : set) {
                nodes[i] = cfgNode;
                i++;
            }
            return B.list(nodes);
        });
    }

}
