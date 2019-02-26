package mb.flowspec.runtime.interpreter.patterns;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.Types;

public class StringLiteralPatternNode extends PatternNode {
    private final String value;

    public StringLiteralPatternNode(String value) {
        this.value = value;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        return Types.isString(value) && Types.asString(value).equals(this.value);
    }
}
