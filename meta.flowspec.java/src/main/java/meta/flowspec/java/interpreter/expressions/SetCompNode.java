package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import io.usethesource.capsule.Set;
import meta.flowspec.java.interpreter.UnreachableException;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

public class SetCompNode extends ExpressionNode {
    public final ExpressionNode expression;
    public final PatternNode[] sourcePatterns;
    public final ExpressionNode[] sources;
    public final SetCompPredicateNode[] predicates;

    public SetCompNode(ExpressionNode expression, PatternNode[] sourcePatterns, ExpressionNode[] sources,
            SetCompPredicateNode[] predicates) {
        this.expression = expression;
        this.sourcePatterns = sourcePatterns;
        this.sources = sources;
        this.predicates = predicates;
    }

    public static SetCompNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor,
            IControlFlowGraph<ICFGNode> cfg) {
        return new SetCompNode(ExpressionNode.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg),
                PatternNode.Array.fromIStrategoTerm(appl.getSubterm(1), frameDescriptor, cfg),
                ExpressionNode.Array.fromIStrategoTerm(appl.getSubterm(2), frameDescriptor, cfg),
                SetCompPredicateNode.Array.fromIStrategoTerm(appl.getSubterm(3), frameDescriptor, cfg));
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return executeSet(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException();
        }
    }

    @Override
    public Set.Immutable<?> executeSet(VirtualFrame frame) throws UnexpectedResultException {
        // FIXME For now we're assuming exactly one source
        Set.Immutable<?> set = sources[0].executeSet(frame);
        Set.Transient<Object> result = Set.Transient.of();
        for(Object value : set) {
            this.sourcePatterns[0].executeGeneric(frame, value);
            boolean keep = true;
            for (SetCompPredicateNode pred : predicates) {
                keep |= pred.executeBoolean(frame);
            }
            if (keep) {
                result.__insert(expression.executeGeneric(frame));
            }
        }
        return result.freeze();
    }
}