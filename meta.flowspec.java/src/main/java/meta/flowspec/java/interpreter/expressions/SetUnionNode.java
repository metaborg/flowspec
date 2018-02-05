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
public abstract class SetUnionNode extends ExpressionNode {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected Set union(Set left, Set right) {
        return new Set(io.usethesource.capsule.Set.Immutable.union(left.set, right.set));
    }

    public static IMatcher<SetUnionNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl2("SetUnion", 
                ExpressionNode.matchExpr(frameDescriptor, solution), 
                ExpressionNode.matchExpr(frameDescriptor, solution),
                (appl, e1, e2) -> {
                    return SetUnionNodeGen.create(e1, e2);
                });
    }
}
