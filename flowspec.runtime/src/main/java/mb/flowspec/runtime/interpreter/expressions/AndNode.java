package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class AndNode extends ExpressionNode {
    @Specialization
    protected boolean and(boolean left, boolean right) {
        return left && right;
    }

    @Specialization
    protected boolean and(Object left, Object right) {
        return Types.asBoolean(left) && Types.asBoolean(right);
    }
}
