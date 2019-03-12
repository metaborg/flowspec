package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.values.EmptyMapOrSet;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.ISet;

public class EmptySetOrMapLiteral extends ExpressionNode {
    public EmptySetOrMapLiteral() {
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        return executeIMap(frame);
    }

    @Override public ISet<IStrategoTerm> executeISet(VirtualFrame frame) {
        return new EmptyMapOrSet<>();
    }

    @Override public IMap<IStrategoTerm, IStrategoTerm> executeIMap(VirtualFrame frame) {
        return new EmptyMapOrSet<>();
    }
}
