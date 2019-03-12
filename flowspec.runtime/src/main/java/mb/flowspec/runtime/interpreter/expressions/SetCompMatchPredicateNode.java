package mb.flowspec.runtime.interpreter.expressions;

import mb.flowspec.runtime.interpreter.patterns.PatternNode;

public class SetCompMatchPredicateNode extends CompPredicateNode {
    public final PatternNode[] arms;

    public SetCompMatchPredicateNode(ExpressionNode expr, PatternNode[] arms) {
        super(expr);
        this.arms = arms;
    }
}
