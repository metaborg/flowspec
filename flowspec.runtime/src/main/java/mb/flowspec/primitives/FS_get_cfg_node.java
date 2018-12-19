package mb.flowspec.primitives;

import java.util.List;
import java.util.Optional;

import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.ControlFlowGraphBuilder;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.terms.M;
import mb.flowspec.terms.TermIndex;

public class FS_get_cfg_node extends AnalysisPrimitive {

    public FS_get_cfg_node() {
        super(FS_get_cfg_node.class.getSimpleName(), 1);
    }

    @Override public Optional<? extends IStrategoTerm> call(IFlowSpecSolution solution, IStrategoTerm term, List<IStrategoTerm> terms)
        throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: nodeKind");
        }
        return M.maybe(() -> {
            final ICFGNode.Kind kind = ControlFlowGraphBuilder.kind(terms.get(0));
            final TermIndex index = ControlFlowGraphBuilder.termIndex(term);
            return solution.controlFlowGraph().findNode(index, kind).get();
        });
    }

}
