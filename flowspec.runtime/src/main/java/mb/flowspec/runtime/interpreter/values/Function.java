package mb.flowspec.runtime.interpreter.values;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import mb.flowspec.runtime.interpreter.Context;
import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.runtime.interpreter.expressions.ExpressionNode;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;

@TypeSystemReference(Types.class)
public class Function extends RootNode {
    private final ArgToVarNode[] arguments;
    private final ExpressionNode body;

    public Function(FrameDescriptor frameDescriptor, ArgToVarNode[] arguments, ExpressionNode body) {
        this(null, frameDescriptor, arguments, body);
    }

    public Function(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor, ArgToVarNode[] arguments,
            ExpressionNode body) {
        super(language, frameDescriptor);
        this.arguments = arguments;
        this.body = body;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        for (ArgToVarNode pv : arguments) {
            pv.execute(frame);
        }
        return body.executeGeneric(frame);
    }
}
