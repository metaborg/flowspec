package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class AndNode extends ExpressionNode {
    protected ExpressionNode[] children;

    @Specialization
    protected boolean and(boolean left, boolean right) {
        return left && right;
    }

    public static IMatcher<AndNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("And", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor), 
                (appl, e1, e2) -> {
                    AndNode result = AndNodeGen.create(e1, e2);
                    result.children = new ExpressionNode[] {e1, e2};
                    return result;
                });
    }

    public void init(InitValues initValues) {
        for (ExpressionNode child : children) {
            child.init(initValues);
        }
    }
}
