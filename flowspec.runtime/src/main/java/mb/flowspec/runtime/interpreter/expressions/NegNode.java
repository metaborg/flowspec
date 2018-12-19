package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChildren({ @NodeChild("number") })
public abstract class NegNode extends ExpressionNode {
    @Specialization protected int negate(int number) {
        return -number;
    }
}
