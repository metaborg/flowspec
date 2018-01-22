package meta.flowspec.java.interpreter.patterns;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class StringLiteralPatternNode extends PatternNode {
    private final String value;

    public StringLiteralPatternNode(String value) {
        this.value = value;
    }

    @Override
    public boolean matchGeneric(VirtualFrame frame, Object value) {
        return value instanceof Integer && executeString(frame, (String) value);
    }

    public boolean executeString(VirtualFrame frame, String value) {
        return this.value == value;
    }

    public static StringLiteralPatternNode fromIStrategoAppl(IStrategoAppl appl) {
        return new StringLiteralPatternNode(Tools.javaStringAt(appl, 0));
    }

    public static IMatcher<StringLiteralPatternNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl1("String", M.stringValue(), (appl, string) -> new StringLiteralPatternNode(string));
    }
}
