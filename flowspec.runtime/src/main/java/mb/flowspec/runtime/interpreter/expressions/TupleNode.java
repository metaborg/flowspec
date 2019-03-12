package mb.flowspec.runtime.interpreter.expressions;

import java.util.Arrays;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.terms.B;

public class TupleNode extends ExpressionNode {
    @Children private final ExpressionNode[] children;

    public TupleNode(ExpressionNode[] children) {
        super();
        this.children = children;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        return executeIStrategoTerm(frame);
    }

    @Override public IStrategoTerm executeIStrategoTerm(VirtualFrame frame) {
        IStrategoTerm[] childVals = Arrays.stream(children).map(c -> { // Java streams and exceptions... smh
            try {
                return c.executeIStrategoTerm(frame);
            } catch(UnexpectedResultException e) {
                throw new TypeErrorException(e);
            }
        }).toArray(IStrategoTerm[]::new);
        return B.tuple(childVals);
    }
}
