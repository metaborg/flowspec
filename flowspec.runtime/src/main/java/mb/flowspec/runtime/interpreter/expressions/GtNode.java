package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class GtNode extends ExpressionNode {
    @Specialization protected boolean gt(int left, int right) {
        return left > right;
    }

    @Specialization protected boolean gt(Object left, Object right) {
        return gt(Types.asInteger(left), Types.asInteger(right));
    }
}
