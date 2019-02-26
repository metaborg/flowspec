package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class ModNode extends ExpressionNode {
    @Specialization protected int modulo(int left, int right) {
        return left % right;
    }

    @Specialization protected int modulo(Object left, Object right) {
        return modulo(Types.asInteger(left), Types.asInteger(right));
    }
}
