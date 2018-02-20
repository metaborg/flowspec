package meta.flowspec.java.interpreter.patterns;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.InitValues;

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
