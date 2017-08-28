package meta.flowspec.java.interpreter.expressions;

import org.pcollections.PSet;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetMinusNode extends ExpressionNode {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected PSet<Object> minus(PSet left, PSet right) {
        return left.minusAll(right);
    }
    
    public static SetMinusNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return
            SetMinusNodeGen.create(
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg),
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(1), frameDescriptor, cfg));
    }
}
