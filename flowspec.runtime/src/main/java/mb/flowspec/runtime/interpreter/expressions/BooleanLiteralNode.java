package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.terms.B;

public class BooleanLiteralNode extends ExpressionNode {
    public static final IStrategoAppl FALSE_TERM = B.appl("False");
    public static final IStrategoAppl TRUE_TERM = B.appl("True");
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
}
