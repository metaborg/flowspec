package mb.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.java.interpreter.InitValues;
import mb.flowspec.java.interpreter.values.Set;
import meta.flowspec.java.interpreter.expressions.SetIntersectNodeGen;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetIntersectNode extends ExpressionNode {
    protected ExpressionNode[] children;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected Set intersect(Set left, Set right) {
        if (left.set == null) { // handle symbolic value of set with everything in it
            return right;
        }
        if (right.set == null) { // handle symbolic value of set with everything in it
            return left;
        }
        return new Set(io.usethesource.capsule.Set.Immutable.intersect(left.set, right.set));
    }

    public static IMatcher<SetIntersectNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("SetIntersect", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, e1, e2) -> {
                    SetIntersectNode result = SetIntersectNodeGen.create(e1, e2);
                    result.children = new ExpressionNode[] {e1,e2};
                    return result;
                });
    }
    
    public void init(InitValues initValues) {
        for (ExpressionNode child : children) {
            child.init(initValues);
        }
    }
}
