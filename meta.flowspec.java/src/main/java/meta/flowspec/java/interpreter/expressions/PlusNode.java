package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.InitValues;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class PlusNode extends ExpressionNode {
    protected ExpressionNode[] children;
    
    @Specialization
    protected int plus(int left, int right) {
        return left + right;
    }

    public static IMatcher<PlusNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Plus", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, e1, e2) -> {
                    PlusNode result = PlusNodeGen.create(e1, e2);
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
