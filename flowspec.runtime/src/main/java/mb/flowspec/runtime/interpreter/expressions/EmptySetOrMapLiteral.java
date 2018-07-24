package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.values.EmptyMapOrSet;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class EmptySetOrMapLiteral extends ExpressionNode {
    public EmptySetOrMapLiteral() {}

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeIMap(frame);
    }

    @Override
    public ISet<ITerm> executeISet(VirtualFrame frame) {
        return new EmptyMapOrSet<>();
    }

    @Override
    public IMap<ITerm, ITerm> executeIMap(VirtualFrame frame) {
        return new EmptyMapOrSet<>();
    }

    @Override
    public void init(InitValues initValues) {}

    public static IMatcher<EmptySetOrMapLiteral> match(FrameDescriptor frameDescriptor) {
        return M.appl0("EmptySetOrMapLiteral",
                appl -> new EmptySetOrMapLiteral());
    }
}
