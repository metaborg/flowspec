package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
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

    public static IMatcher<SetContainsNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl2("SetContains", 
                ExpressionNode.matchExpr(frameDescriptor, cfg), 
                ExpressionNode.matchExpr(frameDescriptor, cfg),
                (appl, e1, e2) -> SetContainsNodeGen.create(e1, e2));
    }
}
