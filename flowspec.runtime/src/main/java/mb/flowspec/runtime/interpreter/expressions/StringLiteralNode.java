package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

public class StringLiteralNode extends ExpressionNode {
    private final String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String executeGeneric(VirtualFrame frame) {
        return value;
    }
}
