package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.UnreachableException;
import mb.flowspec.runtime.interpreter.patterns.PatternNode;
import mb.flowspec.runtime.interpreter.values.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.terms.unification.PersistentUnifier;
import mb.nabl2.util.ImmutableTuple2;

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
    public ITerm executeITerm(VirtualFrame frame) {
        try {
            return executeISet(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException(e);
        }
    }

    @Override
    public Set<ITerm> executeISet(VirtualFrame frame) throws UnexpectedResultException {
        // FIXME For now we're assuming exactly one source
        io.usethesource.capsule.Set.Immutable<ITerm> set = sources[0].executeISet(frame).getSet();
        io.usethesource.capsule.Set.Transient<ITerm> result = io.usethesource.capsule.Set.Transient.of();
        for(Object value : set) {
            boolean keep = this.sourcePatterns[0].matchGeneric(frame, value);
            for (CompPredicateNode pred : predicates) {
                keep &= pred.executeBoolean(frame);
            }
            if (keep) {
                result.__insert(expression.executeITerm(frame));
            }
        }
        return new Set<>(result.freeze());
    }

    public static IMatcher<SetCompNode> match(FrameDescriptor frameDescriptor) {
        /* NOTE: this is in a strange order because the matching construct builds an interpreter AST while
         * doing side-effects on the frameDescriptor. Therefore definitions need to be built before
         * references are built in the AST, because references are immediately resolved through the
         * frameDescriptor. If you do this in the wrong order the AST will contain nulls instead of frame
         * Slots. 
         */
        return M.appl4("SetComp", 
                M.term(),
                M.listElems(PatternNode.matchPattern(frameDescriptor)),
                M.listElems(ExpressionNode.matchExpr(frameDescriptor)),
                M.listElems(CompPredicateNode.matchPred(frameDescriptor)),
                (appl, term, patterns, exprs, preds) -> ImmutableTuple2.of(term, ImmutableTuple2.of(patterns, ImmutableTuple2.of(exprs, preds))))
                .flatMap(tuple -> ExpressionNode.matchExpr(frameDescriptor).match(tuple._1(), PersistentUnifier.Immutable.of()).map(expr -> {
                    List<PatternNode> patterns = tuple._2()._1();
                    List<ExpressionNode> exprs = tuple._2()._2()._1();
                    List<CompPredicateNode> preds = tuple._2()._2()._2();
                    return new SetCompNode(expr, 
                        patterns.toArray(new PatternNode[patterns.size()]), 
                        exprs.toArray(new ExpressionNode[exprs.size()]), 
                        preds.toArray(new CompPredicateNode[preds.size()]));
                }));
    }

    public void init(InitValues initValues) {
        expression.init(initValues);
        for (PatternNode sourcePattern : sourcePatterns) {
            sourcePattern.init(initValues);
        }
        for (ExpressionNode source : sources) {
            source.init(initValues);
        }
        for (CompPredicateNode predicate : predicates) {
            predicate.init(initValues);
        }
    }
}
