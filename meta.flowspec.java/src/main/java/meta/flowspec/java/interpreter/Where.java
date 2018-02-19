package meta.flowspec.java.interpreter;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

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
    
    public void init(ISolution solution) {
        body.init(solution);
        for(WriteVarNode binding : bindings) {
            binding.init(solution);
        }
    }

    public static IMatcher<Where> match(FrameDescriptor frameDescriptor) {
        return M.appl2(
                "Where", 
                ExpressionNode.matchExpr(frameDescriptor),
                M.listElems(WriteVarNode.match(frameDescriptor)), 
                (appl, body, writeVars) -> {
                    return new Where(writeVars.toArray(new WriteVarNode[writeVars.size()]), body);
                });
    }
}
