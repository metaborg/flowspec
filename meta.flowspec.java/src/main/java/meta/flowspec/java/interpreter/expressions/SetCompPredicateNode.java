package meta.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.InitValues;

class SetCompPredicateNode {
    public final ExpressionNode expr;

    public SetCompPredicateNode(ExpressionNode expr) {
        this.expr = expr;
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return this.expr.executeBoolean(frame);
    }

    public static IMatcher<SetCompPredicateNode> matchPred(FrameDescriptor frameDescriptor) {
        return M.cases(
            M.appl1("Predicate", 
                ExpressionNode.matchExpr(frameDescriptor), 
                (appl, expr) -> new SetCompPredicateNode(expr)),
            SetCompMatchPredicateNode.match(frameDescriptor)
        );
    }

    public void init(InitValues initValues) {
        expr.init(initValues);
    }
}
