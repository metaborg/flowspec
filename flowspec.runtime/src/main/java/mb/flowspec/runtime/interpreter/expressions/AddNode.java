package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class AddNode extends ExpressionNode {
    @Specialization
    protected int plus(int left, int right) {
        return left + right;
    }

    @Specialization
    protected int plus(Object left, Object right) {
        return plus(Types.asInteger(left), Types.asInteger(right));
    }
}
