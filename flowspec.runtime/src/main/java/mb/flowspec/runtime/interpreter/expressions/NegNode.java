package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({ @NodeChild("number") })
public abstract class NegNode extends ExpressionNode {
    @Specialization protected int negate(int number) {
        return -number;
    }

    @Specialization protected int negate(Object number) {
        return negate(Types.asInteger(number));
    }
}
