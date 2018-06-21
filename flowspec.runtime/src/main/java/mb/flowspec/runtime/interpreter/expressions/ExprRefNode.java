package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.interpreter.expressions.LatticeItemRefNode.LatticeItem;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public abstract class ExprRefNode extends ExpressionNode {
    public static IMatcher<ExprRefNode> matchExprRef(FrameDescriptor frameDescriptor) {
        return M.cases(
                RefNode.matchRef(frameDescriptor),
                M.appl1("TopOf", M.stringValue(), (appl, string) -> new LatticeItemRefNode(LatticeItem.Top, string)),
                M.appl1("BottomOf", M.stringValue(), (appl, string) -> new LatticeItemRefNode(LatticeItem.Bottom, string))
        );
    }

}
