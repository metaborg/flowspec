package meta.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.InitValues;
import meta.flowspec.java.interpreter.locals.ReadVarNode;
import meta.flowspec.java.solver.UnimplementedException;

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
