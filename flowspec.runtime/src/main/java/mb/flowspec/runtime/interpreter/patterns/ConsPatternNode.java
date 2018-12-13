package mb.flowspec.runtime.interpreter.patterns;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Optional;

import org.metaborg.util.optionals.Optionals;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.IListTerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class ConsPatternNode extends PatternNode {
    private final PatternNode headPattern;
    private final PatternNode tailPattern;

    public ConsPatternNode(PatternNode headPattern, PatternNode tailPattern) {
        this.headPattern = headPattern;
        this.tailPattern = tailPattern;
    }

    @Override public boolean matchGeneric(VirtualFrame frame, Object value) {
        if(!(value instanceof IListTerm)) {
            return false;
        }
        return matchList(frame, (IListTerm) value);
    }

    private boolean matchList(VirtualFrame frame, IListTerm value) {
        return M.cons(
            (h, u) -> Optionals.when(headPattern.matchGeneric(frame, h)), 
            (t, u) -> Optionals.when(tailPattern.matchGeneric(frame, t)), 
            (c, h, t) -> c
        ).match(value).isPresent();
    }

    @Override public void init(InitValues initValues) {
        headPattern.init(initValues);
        tailPattern.init(initValues);
    }

    public static IMatcher<ConsPatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Term", 
                M.string(s -> Optionals.when(s.getValue().equals("Cons"))).flatMap(o -> o),
                M.listElems(PatternNode.matchPattern(frameDescriptor))
                    .flatMap(l -> l.size() == 2 ? Optional.of(l) : Optional.empty()),
            (appl, consname, childPatterns) -> {
                return new ConsPatternNode(childPatterns.get(0), childPatterns.get(1));
            });
    }
}
