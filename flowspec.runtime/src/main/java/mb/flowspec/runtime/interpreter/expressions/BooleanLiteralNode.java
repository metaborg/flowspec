package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.Initializable;
import org.spoofax.terms.util.TermUtils;

public class BooleanLiteralNode extends ExpressionNode implements Initializable {
    private static IStrategoAppl FALSE_TERM;
    private static IStrategoAppl TRUE_TERM;
    private final boolean boolValue;
    private IStrategoAppl value;

    public BooleanLiteralNode(boolean value) {
        this.boolValue = value;
    }

    @Override public IStrategoTerm executeIStrategoTerm(VirtualFrame frame) {
        if(value == null) {
            value = booleanToTerm(boolValue);
        }
        return value;
    }

    @Override public boolean executeBoolean(VirtualFrame frame) {
        return boolValue;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        return executeBoolean(frame);
    }

    @Override public void init(InitValues initValues) {
        BooleanLiteralNode.FALSE_TERM = initValues.termBuilder.applShared("False");
        BooleanLiteralNode.TRUE_TERM = initValues.termBuilder.applShared("True");
    }

    public static IStrategoAppl booleanToTerm(boolean b) {
        return b ? TRUE_TERM : FALSE_TERM;
    }

    public static boolean isTrueTerm(Object o) {
        if(!(o instanceof IStrategoTerm)) return false;
        return TermUtils.isAppl((IStrategoTerm)o, "True", 0);
    }

    public static boolean isFalseTerm(Object o) {
        if(!(o instanceof IStrategoTerm)) return false;
        return TermUtils.isAppl((IStrategoTerm)o, "False", 0);
    }
}
