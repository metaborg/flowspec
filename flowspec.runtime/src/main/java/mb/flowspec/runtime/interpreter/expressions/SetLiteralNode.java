package mb.flowspec.runtime.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.values.Set;

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

    public static IMatcher<SetLiteralNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("SetLiteral", 
                M.listElems(ExpressionNode.matchExpr(frameDescriptor)),
                (appl, exprs) -> new SetLiteralNode(exprs.toArray(new ExpressionNode[exprs.size()])));
    }

    public void init(InitValues initValues) {
        for (ExpressionNode value : values) {
            value.init(initValues);
        }
    }
}
