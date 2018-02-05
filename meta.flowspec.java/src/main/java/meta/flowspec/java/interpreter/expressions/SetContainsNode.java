package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.values.Set;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class SetContainsNode extends ExpressionNode {
    @SuppressWarnings("rawtypes")
    @Specialization
    protected boolean contains(Set left, Object right) {
        if (right == null) {
            return left == null;
        }
        if (!(right instanceof Set)) {
            return false;
        }
        return left.set.contains(((Set) right).set);
    }

    public static IMatcher<SetContainsNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl2("SetContains", 
                ExpressionNode.matchExpr(frameDescriptor, solution), 
                ExpressionNode.matchExpr(frameDescriptor, solution),
                (appl, e1, e2) -> SetContainsNodeGen.create(e1, e2));
    }
}
