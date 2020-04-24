package mb.flowspec.runtime.interpreter.patterns;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.spoofax.terms.util.TermUtils;

public class TermPatternNode extends PatternNode {
    private final String consName;
    private final PatternNode[] childPatterns;

    public TermPatternNode(String consName, PatternNode[] childPatterns) {
        this.consName = consName;
        this.childPatterns = childPatterns;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        if(!(value instanceof IStrategoTerm) || !TermUtils.isAppl((IStrategoTerm)value)) {
            return false;
        }
        return matchAppl(frame, (IStrategoAppl) value);
    }

    private boolean matchAppl(VirtualFrame frame, IStrategoAppl value) {
        if(!value.getName().equals(consName) || value.getSubtermCount() != childPatterns.length) {
            return false;
        }
        boolean result = true;
        IStrategoTerm[] args = value.getAllSubterms();
        for(int i = 0; i < childPatterns.length; i++) {
            result &= childPatterns[i].matchGeneric(frame, args[i]);
        }
        return result;
    }
}
