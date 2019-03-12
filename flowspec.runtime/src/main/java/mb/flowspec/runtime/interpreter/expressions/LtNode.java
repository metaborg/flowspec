package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class LtNode extends ExpressionNode {
    @Specialization protected boolean lt(int left, int right) {
        return left < right;
    }

    @Specialization protected boolean lt(Object left, Object right) {
        return lt(Types.asInteger(left), Types.asInteger(right));
    }
}
