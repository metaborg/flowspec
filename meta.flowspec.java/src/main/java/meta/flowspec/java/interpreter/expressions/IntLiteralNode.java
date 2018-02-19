package meta.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class IntLiteralNode extends ExpressionNode {
    private final int value;
    
    public IntLiteralNode(int value) {
        this.value = value;
    }
    
    @Override
    public int executeInt(VirtualFrame _frame) {
        return value;
    }
    
    @Override
    public Object executeGeneric(VirtualFrame _frame) {
        return value;
    }

    public static IntLiteralNode fromIStrategoAppl(IStrategoAppl appl) {
        return new IntLiteralNode(Integer.valueOf(Tools.javaStringAt(appl, 0)));
    }

    public static IMatcher<IntLiteralNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("Int", M.integerValue(), (appl, i) -> new IntLiteralNode(i));
    }
}
