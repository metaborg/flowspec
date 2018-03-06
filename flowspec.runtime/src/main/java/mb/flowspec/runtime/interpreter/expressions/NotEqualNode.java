package mb.flowspec.runtime.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import java.util.Objects;

import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.expressions.NotEqualNodeGen;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class NotEqualNode extends ExpressionNode {
    protected ExpressionNode[] children;
    
    @Specialization
    protected boolean nequal(int left, int right) {
        return left != right;
    }

    @Specialization
    protected boolean nequal(boolean left, boolean right) {
        return left != right;
    }

    @Specialization
    protected boolean nequal(String left, String right) {
        return !Objects.equals(left, right);
    }

    @Specialization
    protected boolean nequal(ITerm left, ITerm right) {
        return !Objects.equals(left, right);
    }

    /**
     * We covered all the cases that can return true in the type specializations above. If we
     * compare two values with different types, the result is known to be false.
     * <p>
     * Note that the guard is essential for correctness: without the guard, the specialization would
     * also match when the left and right value have the same type. The following scenario would
     * return a wrong value: First, the node is executed with the left value 42 (type long) and the
     * right value "abc" (String). This specialization matches, and since it is the first execution
     * it is also the only specialization. Then, the node is executed with the left value "42" (type
     * long) and the right value "42" (type long). Since this specialization is already present, and
     * (without the guard) also matches (long values can be boxed to Object), it is executed. The
     * wrong return value is "false".
     */
    @Specialization(guards = "left.getClass() != right.getClass()")
    protected boolean equal(Object left, Object right) {
        assert !left.equals(right);
        return true;
    }

    public static IMatcher<NotEqualNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("NEq", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, e1, e2) -> {
                    NotEqualNode result = NotEqualNodeGen.create(e1, e2);
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
