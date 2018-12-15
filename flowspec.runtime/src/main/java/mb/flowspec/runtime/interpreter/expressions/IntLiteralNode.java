package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

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

    public static IMatcher<IntLiteralNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("Int", M.stringValue().map(Integer::parseInt), (appl, i) -> new IntLiteralNode(i));
    }

    @Override
    public void init(InitValues initValues) {
        // Do nothing
    }
}
