package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple2;

import com.google.common.collect.ImmutableList;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.UnreachableException;
import meta.flowspec.java.interpreter.patterns.PatternNode;
import meta.flowspec.java.interpreter.values.Set;

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

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return executeSet(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException();
        }
    }

    @Override
    public ITerm executeITerm(VirtualFrame frame) {
        try {
            return executeSet(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException();
        }
    }

    @Override
    public Set<ITerm> executeSet(VirtualFrame frame) throws UnexpectedResultException {
        // FIXME For now we're assuming exactly one source
        io.usethesource.capsule.Set.Immutable<ITerm> set = sources[0].executeSet(frame).set;
        io.usethesource.capsule.Set.Transient<ITerm> result = io.usethesource.capsule.Set.Transient.of();
        for(Object value : set) {
            boolean keep = this.sourcePatterns[0].matchGeneric(frame, value);
            for (SetCompPredicateNode pred : predicates) {
                keep &= pred.executeBoolean(frame);
            }
            if (keep) {
                result.__insert((ITerm) expression.executeGeneric(frame));
            }
        }
        return new Set<>(result.freeze());
    }

    public static IMatcher<SetCompNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl4("SetComp", 
                M.term(),
                M.listElems(PatternNode.matchPattern(frameDescriptor, solution)),
                M.listElems(ExpressionNode.matchExpr(frameDescriptor, solution)),
                M.listElems(SetCompPredicateNode.matchPred(frameDescriptor, solution)),
                (appl, term, patterns, exprs, preds) -> ImmutableTuple2.of(term, ImmutableTuple2.of(patterns, ImmutableTuple2.of(exprs, preds))))
                .flatMap(tuple -> ExpressionNode.matchExpr(frameDescriptor, solution).match(tuple._1()).map(expr -> {
                    ImmutableList<PatternNode> patterns = tuple._2()._1();
                    ImmutableList<ExpressionNode> exprs = tuple._2()._2()._1();
                    ImmutableList<SetCompPredicateNode> preds = tuple._2()._2()._2();
                    return new SetCompNode(expr, 
                        patterns.toArray(new PatternNode[patterns.size()]), 
                        exprs.toArray(new ExpressionNode[exprs.size()]), 
                        preds.toArray(new SetCompPredicateNode[preds.size()]));
                }));
    }
}
