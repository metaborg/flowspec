package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class CompPredicateNode {
    public final ExpressionNode expr;

    public CompPredicateNode(ExpressionNode expr) {
        this.expr = expr;
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return this.expr.executeBoolean(frame);
    }
}
