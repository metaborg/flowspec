package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

class SetCompPredicateNode {
    public final ExpressionNode expr;

    public SetCompPredicateNode(ExpressionNode expr) {
        this.expr = expr;
    }

    public static SetCompPredicateNode fromIStrategoTerm(IStrategoTerm term, FrameDescriptor frameDescriptor,
            IControlFlowGraph<ICFGNode> cfg) {
        assert term instanceof IStrategoAppl : "Expected a constructor application term";
        final IStrategoAppl appl = (IStrategoAppl) term;
        switch (appl.getConstructor().getName()) {
        case "Predicate": {
            assert appl.getSubtermCount() == 1 : "Expected Predicate to have 1 child";
            return new SetCompPredicateNode(ExpressionNode.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg));
        }
        case "MatchPredicate": {
            assert appl.getSubtermCount() == 2 : "Expected Predicate to have 1 child";
            return SetCompMatchPredicateNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
        }
        default:
            throw new IllegalArgumentException(
                    "Unknown constructor for SetCompSource: " + appl.getConstructor().getName());
        }
    }

    public static class Array {
        public static SetCompPredicateNode[] fromIStrategoTerm(IStrategoTerm term, FrameDescriptor frameDescriptor,
                IControlFlowGraph<ICFGNode> cfg) {
            assert term instanceof IStrategoList : "Expected a list term";
            final IStrategoList list = (IStrategoList) term;
            SetCompPredicateNode[] result = new SetCompPredicateNode[term.getSubtermCount()];
            int i = 0;
            for (IStrategoTerm sourceTerm : list) {
                result[i] = SetCompPredicateNode.fromIStrategoTerm(sourceTerm, frameDescriptor, cfg);
                i++;
            }
            return result;
        }
    }

    public boolean executeBoolean(VirtualFrame frame) {
        // TODO Auto-generated method stub
        return false;
    }
}