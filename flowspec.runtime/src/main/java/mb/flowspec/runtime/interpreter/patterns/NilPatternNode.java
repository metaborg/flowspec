package mb.flowspec.runtime.interpreter.patterns;

import static mb.nabl2.terms.matching.TermMatch.M;

import org.metaborg.util.optionals.Optionals;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.IListTerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class NilPatternNode extends PatternNode {
    public static final NilPatternNode SINGLETON = new NilPatternNode();

    protected NilPatternNode() {
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        if(!(value instanceof IListTerm)) {
            return false;
        }
        return matchList(frame, (IListTerm) value);
    }

    private boolean matchList(VirtualFrame frame, IListTerm value) {
        return M.nil(n -> n).match(value).isPresent();
    }

    @Override public void init(InitValues initValues) {
    }

    public static IMatcher<NilPatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Term", 
                M.string(s -> Optionals.when(s.getValue().equals("Nil"))).flatMap(o -> o),
                M.listElems(PatternNode.matchPattern(frameDescriptor))
                    .flatMap(l -> Optionals.when(l.isEmpty())),
            (appl, consname, childPatterns) -> {
                return NilPatternNode.SINGLETON;
            });
    }
}
