package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Set;

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
    public ISet<IStrategoTerm> executeISet(VirtualFrame frame) {
        io.usethesource.capsule.Set.Transient<IStrategoTerm> set = io.usethesource.capsule.Set.Transient.of();
        for (ExpressionNode expr : values) {
            try {
                set.__insert(expr.executeIStrategoTerm(frame));
            } catch (UnexpectedResultException e) {
                throw new RuntimeException(e);
            }
        }
        return new Set<>(set.freeze());
    }
}
