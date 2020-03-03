package mb.flowspec.runtime.interpreter.patterns;

import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;

public class ConsPatternNode extends PatternNode {
    private final PatternNode headPattern;
    private final PatternNode tailPattern;

    public ConsPatternNode(PatternNode headPattern, PatternNode tailPattern) {
        this.headPattern = headPattern;
        this.tailPattern = tailPattern;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        if(!(value instanceof IStrategoTerm) || !TermUtils.isList((IStrategoTerm)value)) {
            return false;
        }
        return matchList(frame, (IStrategoList) value);
    }

    private boolean matchList(VirtualFrame frame, IStrategoList value) {
        if(value.isEmpty()) {
            return false;
        }
        return headPattern.matchGeneric(frame, value.head()) && tailPattern.matchGeneric(frame, value.tail());
    }
}
