package mb.flowspec.runtime.interpreter;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import mb.flowspec.runtime.interpreter.expressions.ExpressionNode;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;

public class FlowSpecRootNode extends RootNode {
    private final ArgToVarNode[] arguments;
    private final ExpressionNode body;

    public FlowSpecRootNode(ArgToVarNode[] arguments, ExpressionNode body, FrameDescriptor fd) {
        super(null, fd);
        this.arguments = arguments;
        this.body = body;
    }

    @Override public Object execute(VirtualFrame frame) {
        for(ArgToVarNode arg : arguments) {
            arg.execute(frame);
        }
        return body.executeGeneric(frame);
    }

}
