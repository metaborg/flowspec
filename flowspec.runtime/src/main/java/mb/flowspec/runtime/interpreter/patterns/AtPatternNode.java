package mb.flowspec.runtime.interpreter.patterns;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AtPatternNode extends PatternNode {
    private final VarPatternNode var;
    private final PatternNode pattern;

    public AtPatternNode(VarPatternNode var, PatternNode pattern) {
        this.var = var;
        this.pattern = pattern;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        boolean patternMatchSuccess = var.matchGeneric(frame, value);
        assert patternMatchSuccess;
        return pattern.matchGeneric(frame, value);
    }
}
