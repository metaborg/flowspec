package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import static mb.nabl2.terms.build.TermBuild.B;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class BooleanLiteralNode extends ExpressionNode {
    public static final IApplTerm FALSE_TERM = B.newAppl("False");
    public static final IApplTerm TRUE_TERM = B.newAppl("True");
    private final boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }
    
    @Override
    public ITerm executeITerm(VirtualFrame frame) {
        return value ? TRUE_TERM : FALSE_TERM;
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeBoolean(frame);
    }

    public static IMatcher<BooleanLiteralNode> match(FrameDescriptor frameDescriptor) {
        return M.cases(
                M.appl0("True", (appl) -> new BooleanLiteralNode(true)),
                M.appl0("False", (appl) -> new BooleanLiteralNode(false)));
    }

    @Override
    public void init(InitValues initValues) {
        // do nothing
    }

}
