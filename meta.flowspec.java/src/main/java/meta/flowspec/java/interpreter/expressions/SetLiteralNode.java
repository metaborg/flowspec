package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.values.Set;

public class SetLiteralNode extends ExpressionNode {
    private final ExpressionNode[] values;
    
    public SetLiteralNode(ExpressionNode[] values) {
        this.values = values;
    }
    
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeSet(frame);
    }

    @Override
    public Set<ITerm> executeSet(VirtualFrame frame) {
        io.usethesource.capsule.Set.Transient<ITerm> set = io.usethesource.capsule.Set.Transient.of();
        for (ExpressionNode expr : values) {
            set.__insert((ITerm) expr.executeGeneric(frame));
        }
        return new Set<>(set.freeze());
    }

    public static IMatcher<SetLiteralNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl1("SetLiteral", 
                M.listElems(ExpressionNode.matchExpr(frameDescriptor, cfg)),
                (appl, exprs) -> new SetLiteralNode(exprs.toArray(new ExpressionNode[exprs.size()])));
    }

}
