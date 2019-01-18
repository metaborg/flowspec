package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.Initializable;

public class BooleanLiteralNode extends ExpressionNode implements Initializable {
    public static IStrategoAppl FALSE_TERM;
    public static IStrategoAppl TRUE_TERM;
    private final boolean boolValue;
    private final IStrategoAppl value;

    public BooleanLiteralNode(boolean value) {
        this.boolValue = value;
        this.value = value ? TRUE_TERM : FALSE_TERM;
    }

    @Override public IStrategoTerm executeIStrategoTerm(VirtualFrame frame) {
        return value;
    }

    @Override public boolean executeBoolean(VirtualFrame frame) {
        return boolValue;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        return executeBoolean(frame);
    }

    @Override public void init(InitValues initValues) {
        BooleanLiteralNode.FALSE_TERM = initValues.termBuilder().applShared("False");
        BooleanLiteralNode.TRUE_TERM = initValues.termBuilder().applShared("True");
    }
}
