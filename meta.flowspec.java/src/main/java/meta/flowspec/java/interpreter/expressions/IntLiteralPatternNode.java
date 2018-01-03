package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class IntLiteralPatternNode extends PatternNode {
    private final int value;

    public IntLiteralPatternNode(int value) {
        this.value = value;
    }

    @Override
    public boolean executeGeneric(VirtualFrame frame, Object value) {
        return value instanceof Integer && executeInt(frame, (Integer) value);
    }

    public boolean executeInt(VirtualFrame frame, int value) {
        return this.value == value;
    }

    public static IntLiteralPatternNode fromIStrategoAppl(IStrategoAppl appl) {
        return new IntLiteralPatternNode(Integer.valueOf(Tools.javaStringAt(appl, 0)));
    }

    public static IMatcher<IntLiteralPatternNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl1("Int", M.integerValue(), (appl, i) -> new IntLiteralPatternNode(i));
    }
}
