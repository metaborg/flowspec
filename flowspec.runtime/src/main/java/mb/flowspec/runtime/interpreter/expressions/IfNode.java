package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class IfNode extends ExpressionNode {
    @Child private ExpressionNode condition;

    @Child private ExpressionNode thenBranch;

    @Child private ExpressionNode elseBranch;

    public IfNode(ExpressionNode condition, ExpressionNode thenBranch, ExpressionNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        try {
            if(condition.executeBoolean(frame)) {
                return thenBranch.executeGeneric(frame);
            } else {
                return elseBranch.executeGeneric(frame);
            }
        } catch(UnexpectedResultException e) {
            throw new UnsupportedSpecializationException(this, new Node[] { condition }, e.getResult());
        }
    }
}
