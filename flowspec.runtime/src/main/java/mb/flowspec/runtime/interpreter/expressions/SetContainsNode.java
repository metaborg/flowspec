package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.SymbolicLargestSetException;
import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.ISet;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class SetContainsNode extends ExpressionNode {
    @Specialization
    protected boolean contains(IStrategoTerm left, ISet<?> right) {
        // handle symbolic value of set with everything in it
        try {
            right.getSet();
        } catch (SymbolicLargestSetException e) {
            return true;
        }
        return right.getSet().contains(left);
    }

    @Specialization
    protected boolean contains(IStrategoTerm left, IMap<?,?> right) {
        return right.getMap().containsKey(left);
    }

    @Specialization
    protected boolean contains(Object left, IMap<?,?> right) {
        return contains(Types.asIStrategoTerm(left), right);
    }

    @Specialization
    protected boolean contains(Object left, ISet<?> right) {
        return contains(Types.asIStrategoTerm(left), right);
    }
}
