package mb.flowspec.primitives;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.terms.util.TermUtils;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import mb.flowspec.controlflow.ControlFlowGraphBuilder;
import mb.flowspec.controlflow.ICFGNode;

public class RegisterEdge extends Strategy {
    private final ControlFlowGraphBuilder builder;

    RegisterEdge(ControlFlowGraphBuilder builder) {
        this.builder = builder;
    }

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current) {
        if(!(TermUtils.isTuple(current)) || current.getSubtermCount() != 2) {
            return null;
        }
        final IStrategoTerm left = current.getSubterm(0);
        final IStrategoTerm right = current.getSubterm(1);
        if(!(left instanceof ICFGNode)) {
            return null;
        }
        if(!(right instanceof ICFGNode)) {
            return null;
        }
        this.builder.edges().__insert((ICFGNode) left, (ICFGNode) right);
        return current;
    }
}
