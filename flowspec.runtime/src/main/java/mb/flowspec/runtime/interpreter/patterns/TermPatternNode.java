package mb.flowspec.runtime.interpreter.patterns;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Arrays;
import java.util.List;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class TermPatternNode extends PatternNode {
    private final String consName;
    private final PatternNode[] childPatterns;

    public TermPatternNode(String consName, PatternNode[] childPatterns) {
        this.consName = consName;
        this.childPatterns = childPatterns;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        if(!(value instanceof IApplTerm)) {
            return false;
        }
        return matchAppl(frame, (IApplTerm) value);
    }

    private boolean matchAppl(VirtualFrame frame, IApplTerm value) {
        if(!value.getOp().equals(consName) || value.getArity() != childPatterns.length) {
            return false;
        }
        boolean result = true;
        for(int i = 0; i < childPatterns.length; i++) {
            List<ITerm> args = value.getArgs();
            result &= childPatterns[i].matchGeneric(frame, args.get(i));
        }
        return result;
    }

    @Override public void init(InitValues initValues) {
        Arrays.stream(childPatterns).forEach(p -> p.init(initValues));
    }

    public static IMatcher<TermPatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Term", M.stringValue(), M.listElems(PatternNode.matchPattern(frameDescriptor)),
            (appl, consname, childPatterns) -> {
                return new TermPatternNode(consname, childPatterns.toArray(new PatternNode[0]));
            });
    }
}
