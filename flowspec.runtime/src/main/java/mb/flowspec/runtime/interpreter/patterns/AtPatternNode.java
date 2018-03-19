package mb.flowspec.runtime.interpreter.patterns;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class AtPatternNode extends PatternNode {
    private final VarPatternNode var;
    private final PatternNode pattern;

    public AtPatternNode(VarPatternNode var, PatternNode pattern) {
        this.var = var;
        this.pattern = pattern;
    }

    @Override
    public boolean matchGeneric(VirtualFrame frame, Object value) {
        boolean patternMatchSuccess = var.matchGeneric(frame, value);
        assert patternMatchSuccess;
        return pattern.matchGeneric(frame, value);
    }

    public static IMatcher<AtPatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2(
                "At", 
                VarPatternNode.match(frameDescriptor), 
                PatternNode.matchPattern(frameDescriptor), 
                (appl, var, pattern) -> new AtPatternNode(var, pattern));
    }
    
    public void init(InitValues initValues) {
        pattern.init(initValues);
    }
}
