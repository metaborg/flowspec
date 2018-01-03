package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.locals.ReadVarNode;

public class TypeNode extends ExpressionNode {
    @SuppressWarnings("unused")
    private final ReadVarNode occurence;

    public TypeNode(ReadVarNode occurence) {
        super();
        this.occurence = occurence;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw new RuntimeException("Getting the type of an occurence is currently unimplemented");
    }

    public static IMatcher<TypeNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl1("Type", ReadVarNode.match(frameDescriptor), (appl, rvn) -> new TypeNode(rvn));
    }
}
