package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import io.usethesource.capsule.Set;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

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
    public Set.Immutable<Object> executeSet(VirtualFrame frame) {
        Set.Transient<Object> set = Set.Transient.of();
        for (ExpressionNode expr : values) {
            set.__insert(expr.executeGeneric(frame));
        }
        return set.freeze();
    }

    public static SetLiteralNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor,
            IControlFlowGraph<ICFGNode> cfg) {
        ExpressionNode[] expressions = ExpressionNode.Array.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg);
        return new SetLiteralNode(expressions);
    }

}
