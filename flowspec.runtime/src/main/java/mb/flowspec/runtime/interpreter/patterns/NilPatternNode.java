package mb.flowspec.runtime.interpreter.patterns;

import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;

public class NilPatternNode extends PatternNode {
    public static final NilPatternNode SINGLETON = new NilPatternNode();

    protected NilPatternNode() {
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        if (!(value instanceof IStrategoTerm) || !TermUtils.isList((IStrategoTerm)value)) {
            return false;
        }
        return ((IStrategoList) value).isEmpty();
    }
}
