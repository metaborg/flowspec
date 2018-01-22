package meta.flowspec.java.interpreter.patterns;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
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

    public static IMatcher<AtPatternNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl2(
                "At", 
                VarPatternNode.match(frameDescriptor, cfg), 
                PatternNode.matchPattern(frameDescriptor, cfg), 
                (appl, var, pattern) -> new AtPatternNode(var, pattern));
    }
}
