package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.flowspec.runtime.solver.UnimplementedException;

public class TypeNode extends ExpressionNode {
    @SuppressWarnings("unused") private final ReadVarNode occurrence;

    public TypeNode(ReadVarNode occurrence) {
        super();
        this.occurrence = occurrence;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        throw new UnimplementedException("Getting the type of an occurence is currently unimplemented");
    }
}
