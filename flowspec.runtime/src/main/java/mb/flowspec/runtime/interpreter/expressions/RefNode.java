package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class RefNode extends ExprRefNode {
    public abstract Object executeGeneric(VirtualFrame frame);
}
