package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.values.Set;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetMinusNode extends ExpressionNode {
    protected ExpressionNode[] children;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected Set minus(Set left, Set right) {
        return new Set(io.usethesource.capsule.Set.Immutable.subtract(left.set, right.set));
    }

    public static IMatcher<SetMinusNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("SetDifference", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, e1, e2) -> {
                    SetMinusNode result = SetMinusNodeGen.create(e1, e2);
                    result.children = new ExpressionNode[] {e1,e2};
                    return result;
                });
    }
    
    public void init(ISolution solution) {
        for (ExpressionNode child : children) {
            child.init(solution);
        }
    }
}
