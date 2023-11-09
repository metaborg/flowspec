package mb.flowspec.runtime.interpreter.expressions;

import org.metaborg.util.collection.CapsuleUtil;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.UnreachableException;
import mb.flowspec.runtime.interpreter.patterns.PatternNode;
import mb.flowspec.runtime.interpreter.values.Map;
import mb.flowspec.terms.B;
import org.spoofax.terms.util.M;

public class MapCompNode extends ExpressionNode {
    public final ExpressionNode expression;
    public final PatternNode[] sourcePatterns;
    public final ExpressionNode[] sources;
    public final CompPredicateNode[] predicates;

    public MapCompNode(ExpressionNode expression, PatternNode[] sourcePatterns, ExpressionNode[] sources,
        CompPredicateNode[] predicates) {
        this.expression = expression;
        this.sourcePatterns = sourcePatterns;
        this.sources = sources;
        this.predicates = predicates;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        try {
            return executeIMap(frame);
        } catch(UnexpectedResultException e) {
            throw new UnreachableException(e);
        }
    }

    @Override public IStrategoTerm executeIStrategoTerm(VirtualFrame frame) {
        try {
            return executeIMap(frame);
        } catch(UnexpectedResultException e) {
            throw new UnreachableException(e);
        }
    }

    @Override public Map<IStrategoTerm, IStrategoTerm> executeIMap(VirtualFrame frame) throws UnexpectedResultException {
        // FIXME For now we're assuming exactly one source
        io.usethesource.capsule.Map.Immutable<IStrategoTerm, IStrategoTerm> map = sources[0].executeIMap(frame).getMap();
        io.usethesource.capsule.Map.Transient<IStrategoTerm, IStrategoTerm> result = CapsuleUtil.transientMap();
        for(java.util.Map.Entry<IStrategoTerm, IStrategoTerm> value : map.entrySet()) {
            boolean keep = this.sourcePatterns[0].matchGeneric(frame, B.tuple(value.getKey(), value.getValue()));
            for(CompPredicateNode pred : predicates) {
                keep &= pred.executeBoolean(frame);
            }
            if(keep) {
                IStrategoTerm expectedTuple = expression.executeIStrategoTerm(frame);
                IStrategoTuple tuple = M.tuple(expectedTuple, 2);
                result.__put(M.at(tuple, 0), M.at(tuple, 1));
            }
        }
        return new Map<>(result.freeze());
    }
}
