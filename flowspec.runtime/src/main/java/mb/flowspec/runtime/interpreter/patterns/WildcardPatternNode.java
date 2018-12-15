package mb.flowspec.runtime.interpreter.patterns;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class WildcardPatternNode extends PatternNode {
    private static final WildcardPatternNode INSTANCE = new WildcardPatternNode();
    
    private WildcardPatternNode() {
    }
    
    public static WildcardPatternNode of() {
        return INSTANCE;
    }
    
    @Override
    public boolean matchGeneric(VirtualFrame frame, Object value) {
        return true;
    }

    public static IMatcher<WildcardPatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl0("Wildcard", (appl) -> WildcardPatternNode.of());
    }

    @Override
    public void init(InitValues initValues) {
        // Do nothing
    }
}
