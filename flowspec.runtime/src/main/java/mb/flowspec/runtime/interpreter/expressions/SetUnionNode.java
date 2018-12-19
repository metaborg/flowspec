package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.SymbolicLargestSetException;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Map;
import mb.flowspec.runtime.interpreter.values.Set;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetUnionNode extends ExpressionNode {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected ISet union(ISet left, ISet right) {
        // handle symbolic value of set with everything in it
        try {
            left.getSet();
        } catch(SymbolicLargestSetException e) {
            return left;
        }
        try {
            right.getSet();
        } catch(SymbolicLargestSetException e) {
            return right;
        }
        return new Set(io.usethesource.capsule.Set.Immutable.union(left.getSet(), right.getSet()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected IMap union(IMap left, IMap right) {
        return new Map(left.getMap().__putAll(right.getMap()));
    }
}
