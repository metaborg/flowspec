package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.Set;

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
