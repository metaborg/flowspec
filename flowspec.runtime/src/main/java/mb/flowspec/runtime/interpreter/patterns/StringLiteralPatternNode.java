package mb.flowspec.runtime.interpreter.patterns;

import com.oracle.truffle.api.frame.VirtualFrame;

public class StringLiteralPatternNode extends PatternNode {
    private final String value;

    public StringLiteralPatternNode(String value) {
        this.value = value;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        return value instanceof Integer && executeString(frame, (String) value);
    }

    public boolean executeString(VirtualFrame frame, String value) {
        return this.value == value;
    }
}
