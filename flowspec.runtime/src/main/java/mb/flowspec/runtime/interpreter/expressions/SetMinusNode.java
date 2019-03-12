package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.SymbolicLargestSetException;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Set;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetMinusNode extends ExpressionNode {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected ISet minus(ISet left, ISet right) {
        // handle symbolic value of set with everything in it
        try {
            right.getSet();
        } catch(SymbolicLargestSetException e) {
            return new Set();
        }
        return new Set(io.usethesource.capsule.Set.Immutable.subtract(left.getSet(), right.getSet()));
    }
}
