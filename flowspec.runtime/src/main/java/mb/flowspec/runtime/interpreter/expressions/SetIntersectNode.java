package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.SymbolicLargestSetException;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Set;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetIntersectNode extends ExpressionNode {
    protected ExpressionNode[] children;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected ISet intersect(ISet left, ISet right) {
        // handle symbolic value of set with everything in it
        try {
            left.getSet();
        } catch(SymbolicLargestSetException e) {
            return right;
        }
        try {
            right.getSet();
        } catch(SymbolicLargestSetException e) {
            return left;
        }
        return new Set(io.usethesource.capsule.Set.Immutable.intersect(left.getSet(), right.getSet()));
    }
}
