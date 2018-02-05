package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.patterns.PatternNode;

class SetCompMatchPredicateNode extends SetCompPredicateNode {
    public final PatternNode[] arms;

    public SetCompMatchPredicateNode(ExpressionNode expr, PatternNode[] arms) {
        super(expr);
        this.arms = arms;
    }

    public static IMatcher<SetCompMatchPredicateNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl2("MatchPredicate", 
            ExpressionNode.matchExpr(frameDescriptor, solution), 
            M.listElems(PatternNode.matchPattern(frameDescriptor, solution)), 
            (appl, expr, patterns) -> {
                return new SetCompMatchPredicateNode(expr, patterns.toArray(new PatternNode[patterns.size()]));
            });
    }
}