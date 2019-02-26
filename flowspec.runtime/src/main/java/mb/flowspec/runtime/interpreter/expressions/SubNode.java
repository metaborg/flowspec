package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SubNode extends ExpressionNode {
    @Specialization
    protected int subtract(int left, int right) {
        return left - right;
    }

    @Specialization
    protected int subtract(Object left, Object right) {
        return subtract(Types.asInteger(left), Types.asInteger(right));
    }
}
