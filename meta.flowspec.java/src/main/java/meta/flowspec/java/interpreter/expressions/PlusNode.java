package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
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

    public static IMatcher<PlusNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl2("Plus", 
                ExpressionNode.matchExpr(frameDescriptor, cfg), 
                ExpressionNode.matchExpr(frameDescriptor, cfg),
                (appl, e1, e2) -> PlusNodeGen.create(e1, e2));
    }
}
