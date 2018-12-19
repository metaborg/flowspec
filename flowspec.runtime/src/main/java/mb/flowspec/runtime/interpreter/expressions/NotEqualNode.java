package mb.flowspec.runtime.interpreter.expressions;

import java.util.Objects;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class NotEqualNode extends ExpressionNode {
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
    protected boolean nequal(IStrategoTerm left, IStrategoTerm right) {
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
}
