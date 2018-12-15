package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class LtNode extends ExpressionNode {
    protected ExpressionNode[] children;

    @Specialization
    protected boolean or(int left, int right) {
        return left < right;
    }

    public static IMatcher<LtNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Lt", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor), 
                (appl, e1, e2) -> {
                    LtNode result = LtNodeGen.create(e1, e2);
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
