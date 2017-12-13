package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.Set;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

@NodeChildren({ @NodeChild("left"), @NodeChild("right") })
public abstract class SetContainsNode extends ExpressionNode {
    @SuppressWarnings("rawtypes")
    @Specialization
    protected boolean contains(Set left, Object right) {
        if (right == null) {
            return left == null;
        }
        if (!(right instanceof Set)) {
            return false;
        }
        return left.set.contains(((Set) right).set);
    }

    public static SetContainsNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor,
            IControlFlowGraph<ICFGNode> cfg) {
        return SetContainsNodeGen.create(
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg),
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(1), frameDescriptor, cfg));
    }
}
