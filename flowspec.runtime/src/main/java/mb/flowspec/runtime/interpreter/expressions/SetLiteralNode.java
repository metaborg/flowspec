package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class SetLiteralNode extends ExpressionNode {
    private final ExpressionNode[] values;
    
    public SetLiteralNode(ExpressionNode[] values) {
        this.values = values;
    }
    
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeISet(frame);
    }

    @Override
    public ISet<ITerm> executeISet(VirtualFrame frame) {
        io.usethesource.capsule.Set.Transient<ITerm> set = io.usethesource.capsule.Set.Transient.of();
        for (ExpressionNode expr : values) {
            try {
                set.__insert(expr.executeITerm(frame));
            } catch (UnexpectedResultException e) {
                throw new RuntimeException(e);
            }
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
