package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

class CompPredicateNode {
    public final ExpressionNode expr;

    public CompPredicateNode(ExpressionNode expr) {
        this.expr = expr;
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return this.expr.executeBoolean(frame);
    }

    public static IMatcher<CompPredicateNode> matchPred(FrameDescriptor frameDescriptor) {
        return M.cases(
            M.appl1("Predicate", 
                ExpressionNode.matchExpr(frameDescriptor), 
                (appl, expr) -> new CompPredicateNode(expr)),
            SetCompMatchPredicateNode.match(frameDescriptor)
        );
    }

    public void init(InitValues initValues) {
        expr.init(initValues);
    }
}
