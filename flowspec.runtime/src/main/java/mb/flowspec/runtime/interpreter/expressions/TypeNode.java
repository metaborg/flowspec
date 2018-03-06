package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.flowspec.runtime.solver.UnimplementedException;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class TypeNode extends ExpressionNode {
    @SuppressWarnings("unused")
    private final ReadVarNode occurrence;

    public TypeNode(ReadVarNode occurrence) {
        super();
        this.occurrence = occurrence;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw new UnimplementedException("Getting the type of an occurence is currently unimplemented");
    }

    public static IMatcher<TypeNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("Type", ReadVarNode.match(frameDescriptor), (appl, rvn) -> new TypeNode(rvn));
    }

    @Override
    public void init(InitValues initValues) {
        // TODO Auto-generated method stub
        
    }
}
