package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.patterns.PatternNode;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

class SetCompMatchPredicateNode extends SetCompPredicateNode {
    public final PatternNode[] arms;

    public SetCompMatchPredicateNode(ExpressionNode expr, PatternNode[] arms) {
        super(expr);
        this.arms = arms;
    }

    public static IMatcher<SetCompMatchPredicateNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("MatchPredicate", 
            ExpressionNode.matchExpr(frameDescriptor), 
            M.listElems(PatternNode.matchPattern(frameDescriptor)), 
            (appl, expr, patterns) -> {
                return new SetCompMatchPredicateNode(expr, patterns.toArray(new PatternNode[patterns.size()]));
            });
    }

    public void init(InitValues initValues) {
        super.init(initValues);
        for (PatternNode arm : arms) {
            arm.init(initValues);
        }
    }
}
