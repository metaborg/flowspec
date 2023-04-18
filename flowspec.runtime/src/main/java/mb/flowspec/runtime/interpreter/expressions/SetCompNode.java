package mb.flowspec.runtime.interpreter.expressions;

import org.metaborg.util.collection.CapsuleUtil;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.UnreachableException;
import mb.flowspec.runtime.interpreter.patterns.PatternNode;
import mb.flowspec.runtime.interpreter.values.Set;

public class SetCompNode extends ExpressionNode {
    public final ExpressionNode expression;
    public final PatternNode[] sourcePatterns;
    public final ExpressionNode[] sources;
    public final CompPredicateNode[] predicates;

    public SetCompNode(ExpressionNode expression, PatternNode[] sourcePatterns, ExpressionNode[] sources,
            CompPredicateNode[] predicates) {
        this.expression = expression;
        this.sourcePatterns = sourcePatterns;
        this.sources = sources;
        this.predicates = predicates;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return executeISet(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException(e);
        }
    }

    @Override
    public IStrategoTerm executeIStrategoTerm(VirtualFrame frame) {
        try {
            return executeISet(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException(e);
        }
    }

    @Override
    public Set<IStrategoTerm> executeISet(VirtualFrame frame) throws UnexpectedResultException {
        // FIXME For now we're assuming exactly one source
        io.usethesource.capsule.Set.Immutable<IStrategoTerm> set = sources[0].executeISet(frame).getSet();
        io.usethesource.capsule.Set.Transient<IStrategoTerm> result = CapsuleUtil.transientSet();
        for(Object value : set) {
            boolean keep = this.sourcePatterns[0].matchGeneric(frame, value);
            for (CompPredicateNode pred : predicates) {
                keep &= pred.executeBoolean(frame);
            }
            if (keep) {
                result.__insert(expression.executeIStrategoTerm(frame));
            }
        }
        return new Set<>(result.freeze());
    }
}
