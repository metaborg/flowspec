package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class GteNode extends ExpressionNode {
    protected ExpressionNode[] children;

    @Specialization
    protected boolean gte(int left, int right) {
        return left >= right;
    }

    @Specialization
    protected boolean gte(Object left, Object right) {
        return gte(Types.asInteger(left), Types.asInteger(right));
    }
}
