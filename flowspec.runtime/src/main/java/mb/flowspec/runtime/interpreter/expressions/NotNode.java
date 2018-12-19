package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChildren({@NodeChild("expr")})
public abstract class NotNode extends ExpressionNode {
    @Specialization
    protected boolean plus(boolean expr) {
        return !expr;
    }
}
