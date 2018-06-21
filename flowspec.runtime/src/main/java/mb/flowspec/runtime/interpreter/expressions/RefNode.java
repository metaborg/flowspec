package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public abstract class RefNode extends ExprRefNode {
    public abstract Object executeGeneric(VirtualFrame frame);

    public static IMatcher<RefNode> matchRef(FrameDescriptor frameDescriptor) {
        return M.appl1("Ref", ReadVarNode.match(frameDescriptor), (appl, rvn) -> rvn);
    }
}
