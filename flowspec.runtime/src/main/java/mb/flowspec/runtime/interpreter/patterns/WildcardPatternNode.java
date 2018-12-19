package mb.flowspec.runtime.interpreter.patterns;

import com.oracle.truffle.api.frame.VirtualFrame;

public class WildcardPatternNode extends PatternNode {
    private static final WildcardPatternNode INSTANCE = new WildcardPatternNode();

    private WildcardPatternNode() {
    }

    public static WildcardPatternNode of() {
        return INSTANCE;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        return true;
    }
}
