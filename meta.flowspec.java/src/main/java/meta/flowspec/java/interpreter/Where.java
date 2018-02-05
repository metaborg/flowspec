package meta.flowspec.java.interpreter;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import meta.flowspec.java.interpreter.expressions.ExpressionNode;
import meta.flowspec.java.interpreter.locals.WriteVarNode;

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

    public static IMatcher<Where> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl2(
                "Where", 
                ExpressionNode.matchExpr(frameDescriptor, solution),
                M.listElems(WriteVarNode.match(frameDescriptor, solution)), 
                (appl, body, writeVars) -> {
                    return new Where(writeVars.toArray(new WriteVarNode[writeVars.size()]), body);
                });
    }
}
