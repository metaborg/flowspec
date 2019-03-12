package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

public class IntLiteralNode extends ExpressionNode {
    private final int value;

    public IntLiteralNode(int value) {
        this.value = value;
    }

    @Override public int executeInt(VirtualFrame _frame) {
        return value;
    }

    @Override public Object executeGeneric(VirtualFrame _frame) {
        return value;
    }
}
