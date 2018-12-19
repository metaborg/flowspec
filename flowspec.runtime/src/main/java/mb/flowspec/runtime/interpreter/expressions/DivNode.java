package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class DivNode extends ExpressionNode {
    @Specialization
    protected int divide(int left, int right) {
        return left / right;
    }
}
