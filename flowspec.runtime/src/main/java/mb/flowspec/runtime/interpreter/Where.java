package mb.flowspec.runtime.interpreter;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import mb.flowspec.runtime.interpreter.expressions.ExpressionNode;
import mb.flowspec.runtime.interpreter.locals.WriteVarNode;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

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

    public void init(InitValues initValues) {
        body.init(initValues);
        for(WriteVarNode binding : bindings) {
            binding.init(initValues);
        }
    }

    public static IMatcher<Where> match(FrameDescriptor frameDescriptor) {
        return M.appl2(
                "Where", 
                M.term(),
                M.listElems(WriteVarNode.match(frameDescriptor)), 
                (appl, bodyTerm, writeVars) -> {
                    return ExpressionNode.matchExpr(frameDescriptor).match(bodyTerm).map(body -> {
                        return new Where(writeVars.toArray(new WriteVarNode[writeVars.size()]), body);
                    });
                })
                .flatMap(o -> o);
    }
}
