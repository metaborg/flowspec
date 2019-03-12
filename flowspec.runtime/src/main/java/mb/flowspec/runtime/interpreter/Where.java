package mb.flowspec.runtime.interpreter;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import mb.flowspec.runtime.interpreter.expressions.ExpressionNode;
import mb.flowspec.runtime.interpreter.locals.WriteVarNode;

@TypeSystemReference(Types.class)
public class Where extends Node {
    private final WriteVarNode[] bindings;
    private final ExpressionNode body;

    public Where(WriteVarNode[] bindings, ExpressionNode body) {
        super();
        this.bindings = bindings;
        this.body = body;
    }

    public Object execute(VirtualFrame frame) {
        for (WriteVarNode binding : bindings) {
            binding.execute(frame);
        }
        return body.executeGeneric(frame);
    }
}
