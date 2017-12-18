package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.Set;

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

    public static SetLiteralNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor,
            IControlFlowGraph<ICFGNode> cfg) {
        ExpressionNode[] expressions = ExpressionNode.Array.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg);
        return new SetLiteralNode(expressions);
    }

}
