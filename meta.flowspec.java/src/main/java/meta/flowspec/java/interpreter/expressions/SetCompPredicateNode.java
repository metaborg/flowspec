package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

class SetCompPredicateNode {
    public final ExpressionNode expr;

    public SetCompPredicateNode(ExpressionNode expr) {
        this.expr = expr;
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return this.expr.executeBoolean(frame);
    }

    public static IMatcher<SetCompPredicateNode> matchPred(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.cases(
            M.appl1("Predicate", 
                ExpressionNode.matchExpr(frameDescriptor, solution), 
                (appl, expr) -> new SetCompPredicateNode(expr)),
            SetCompMatchPredicateNode.match(frameDescriptor, solution)
        );
    }
}