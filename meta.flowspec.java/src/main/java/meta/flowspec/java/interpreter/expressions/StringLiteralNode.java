package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.InitValues;

public class StringLiteralNode extends ExpressionNode {
    private final String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String executeGeneric(VirtualFrame frame) {
        return value;
    }

    public static StringLiteralNode fromIStrategoAppl(IStrategoAppl appl) {
        return new StringLiteralNode(Tools.javaStringAt(appl, 0));
    }

    public static IMatcher<StringLiteralNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("String", M.stringValue(), (appl, string) -> new StringLiteralNode(string));
    }

    @Override
    public void init(InitValues initValues) {
        // TODO Auto-generated method stub
        
    }
}
