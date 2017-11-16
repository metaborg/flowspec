package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

class SetCompMatchPredicateNode extends SetCompPredicateNode {
    public final PatternNode[] arms;

    public SetCompMatchPredicateNode(ExpressionNode expr, PatternNode[] arms) {
        super(expr);
        this.arms = arms;
    }

    public static SetCompMatchPredicateNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor,
            IControlFlowGraph<ICFGNode> cfg) {
        IStrategoTerm term = appl.getSubterm(1);
        assert term instanceof IStrategoList : "Expected a list term";
        final IStrategoList list = (IStrategoList) term;
        PatternNode[] patterns = new PatternNode[term.getSubtermCount()];
        int i = 0;
        for (IStrategoTerm sourceTerm : list) {
            // Note the extra unwrap, there is a MatchArm constructor we're getting rid of here
            patterns[i] = PatternNode.fromIStrategoTerm(sourceTerm.getSubterm(0), frameDescriptor, cfg);
            i++;
        }
        return new SetCompMatchPredicateNode(ExpressionNode.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg),
                patterns);
    }
}