package meta.flowspec.java.interpreter.patterns;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

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

    public static IMatcher<WildcardPatternNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl0("Wildcard", (appl) -> WildcardPatternNode.of());
    }
}
