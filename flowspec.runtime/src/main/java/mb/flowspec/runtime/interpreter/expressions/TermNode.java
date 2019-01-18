package mb.flowspec.runtime.interpreter.expressions;

import java.util.Arrays;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.Initializable;
import mb.flowspec.terms.B;

public class TermNode extends ExpressionNode implements Initializable {
    private final String consName;
    private final ExpressionNode[] children;
    private IStrategoConstructor cons;

    public TermNode(String consName, ExpressionNode[] children) {
        this.consName = consName;
        this.children = children;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        return B.appl(cons, Arrays.stream(children).map(c -> {
            try {
                return c.executeIStrategoTerm(frame);
            } catch(UnexpectedResultException e) {
                throw new RuntimeException(e);
            }
        }).toArray(i -> new IStrategoTerm[i]));
    }

    @Override public void init(InitValues initValues) {
        cons = initValues.termBuilder().consShared(consName, children.length);
    }
}
