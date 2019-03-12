package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.Types;

@NodeChildren({@NodeChild("expr")})
public abstract class NotNode extends ExpressionNode {
    @Specialization
    protected boolean plus(boolean expr) {
        return !expr;
    }
    
    @Specialization
    protected boolean plus(Object expr) {
        return !Types.asBoolean(expr);
    }
}
