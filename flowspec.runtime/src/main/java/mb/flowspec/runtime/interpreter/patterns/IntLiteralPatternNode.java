package mb.flowspec.runtime.interpreter.patterns;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;

public class IntLiteralPatternNode extends PatternNode {
    private final int value;

    public IntLiteralPatternNode(int value) {
        this.value = value;
    }

    @Override
    public boolean matchGeneric(VirtualFrame frame, Object value) {
        return value instanceof Integer && executeInt(frame, (Integer) value);
    }

    public boolean executeInt(VirtualFrame frame, int value) {
        return this.value == value;
    }

    public static IntLiteralPatternNode fromIStrategoAppl(IStrategoAppl appl) {
        return new IntLiteralPatternNode(Integer.valueOf(Tools.javaStringAt(appl, 0)));
    }

    public static IMatcher<IntLiteralPatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("Int", M.integerValue(), (appl, i) -> new IntLiteralPatternNode(i));
    }

    @Override
    public void init(InitValues initValues) {
        // Do nothing
    }
}
