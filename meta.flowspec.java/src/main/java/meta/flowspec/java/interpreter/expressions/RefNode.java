package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.locals.ReadVarNode;

public abstract class RefNode extends ExpressionNode {
    public abstract Object executeGeneric(VirtualFrame frame);

    public static IMatcher<RefNode> matchRef(FrameDescriptor frameDescriptor) {
        return M.cases(
            M.appl2("QualRef", M.stringValue(), M.stringValue(), (appl, modname, var) -> {
                return new QualRefNode(modname, var);
            }),
            M.appl1("Ref", ReadVarNode.match(frameDescriptor), (appl, rvn) -> rvn)
        );
    }
}