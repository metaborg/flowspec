package mb.flowspec.runtime.interpreter.patterns;

import com.oracle.truffle.api.frame.VirtualFrame;

public class IntLiteralPatternNode extends PatternNode {
    private final int value;

    public IntLiteralPatternNode(int value) {
        this.value = value;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        return value instanceof Integer && executeInt(frame, (Integer) value);
    }

    public boolean executeInt(VirtualFrame frame, int value) {
        return this.value == value;
    }
}
