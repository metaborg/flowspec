package mb.flowspec.primitives;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.ICompleteControlFlowGraph.Transient;

public class RegisterEdge extends Strategy {
    private final Transient cfg;

    RegisterEdge(Transient cfg) {
        this.cfg = cfg;
    }

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current) {
        if(!(current instanceof IStrategoTuple) || current.getSubtermCount() != 2) {
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
        this.cfg.edges().__insert((ICFGNode) left, (ICFGNode) right);
        return current;
    }
}
