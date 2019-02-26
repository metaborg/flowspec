package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class LteNode extends ExpressionNode {
    @Specialization protected boolean lte(int left, int right) {
        return left <= right;
    }

    @Specialization protected boolean lte(Object left, Object right) {
        return lte(Types.asInteger(left), Types.asInteger(right));
    }
}
