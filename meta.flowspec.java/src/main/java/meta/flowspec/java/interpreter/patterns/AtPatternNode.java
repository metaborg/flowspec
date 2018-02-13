package meta.flowspec.java.interpreter.patterns;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

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
    
    public void init(ISolution solution) {
        pattern.init(solution);
    }
}
