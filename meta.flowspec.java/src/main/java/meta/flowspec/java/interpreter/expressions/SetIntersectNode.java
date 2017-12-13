package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.Set;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetIntersectNode extends ExpressionNode {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected Set union(Set left, Set right) {
        return new Set(io.usethesource.capsule.Set.Immutable.intersect(left.set, right.set));
    }
    
    public static SetIntersectNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return
            SetIntersectNodeGen.create(
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg),
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(1), frameDescriptor, cfg));
    }
}
