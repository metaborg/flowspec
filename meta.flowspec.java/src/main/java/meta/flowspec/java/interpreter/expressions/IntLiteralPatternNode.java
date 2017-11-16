package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.VirtualFrame;

public class IntLiteralPatternNode extends PatternNode {
    private final int value;

    public IntLiteralPatternNode(int value) {
        this.value = value;
    }

    @Override
    public boolean executeGeneric(VirtualFrame frame, Object value) {
        return value instanceof Integer && executeInt(frame, (Integer) value);
    }

    public boolean executeInt(VirtualFrame frame, int value) {
        return this.value == value;
    }

    public static IntLiteralPatternNode fromIStrategoAppl(IStrategoAppl appl) {
        return new IntLiteralPatternNode(Integer.valueOf(Tools.javaStringAt(appl, 0)));
    }
}
