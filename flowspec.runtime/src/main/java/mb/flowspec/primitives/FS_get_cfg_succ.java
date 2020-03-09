package mb.flowspec.primitives;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.ControlFlowGraphReader;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.terms.B;
import org.spoofax.terms.util.M;
import mb.flowspec.terms.TermIndexed;

public class FS_get_cfg_succ extends AnalysisPrimitive {

    public FS_get_cfg_succ() {
        super(FS_get_cfg_succ.class.getSimpleName(), 0);
    }

    @Override public Optional<? extends IStrategoTerm> call(IFlowSpecSolution solution, IStrategoTerm term,
        List<IStrategoTerm> terms) throws InterpreterException {
        return M.maybe(() -> {
            ICFGNode node = ControlFlowGraphReader.cfgNode(term);
            final Set<ICFGNode> set = solution.controlFlowGraph().nextNodes(node);
            final IStrategoTerm[] nodes = new IStrategoTerm[set.size()];
            int i = 0;
            for(ICFGNode cfgNode : set) {
                nodes[i] = cfgNode;
                i++;
            }
            return TermIndexed.addTermIndexToAnnos(getFactory(), B.list(nodes));
        });
    }

}
