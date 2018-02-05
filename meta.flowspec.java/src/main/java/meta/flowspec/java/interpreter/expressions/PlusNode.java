package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class PlusNode extends ExpressionNode {
    @Specialization
    protected int plus(int left, int right) {
        return left + right;
    }

    public static IMatcher<PlusNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl2("Plus", 
                ExpressionNode.matchExpr(frameDescriptor, solution), 
                ExpressionNode.matchExpr(frameDescriptor, solution),
                (appl, e1, e2) -> PlusNodeGen.create(e1, e2));
    }
}
