package meta.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.values.Set;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class SetContainsNode extends ExpressionNode {
    protected ExpressionNode[] children;

    @Specialization
    protected boolean contains(ITerm left, Set<?> right) {
        if (right.set == null) { // handle symbolic value of set with everything in it
            return true;
        }
        return right.set.contains(left);
    }

    public static IMatcher<SetContainsNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("SetContains", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, e1, e2) -> {
                    SetContainsNode result = SetContainsNodeGen.create(e1, e2);
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
