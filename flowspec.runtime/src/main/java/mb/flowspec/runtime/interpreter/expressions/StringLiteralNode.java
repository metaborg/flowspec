package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class StringLiteralNode extends ExpressionNode {
    private final String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String executeGeneric(VirtualFrame frame) {
        return value;
    }

    public static IMatcher<StringLiteralNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("String", M.stringValue(), (appl, string) -> new StringLiteralNode(string));
    }

    @Override
    public void init(InitValues initValues) {
        // do nothing
    }
}
