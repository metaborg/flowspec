package meta.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.InitValues;

public class IfNode extends ExpressionNode {
    @Child
    private ExpressionNode condition;

    @Child
    private ExpressionNode thenBranch;

    @Child
    private ExpressionNode elseBranch;

    public IfNode(ExpressionNode condition, ExpressionNode thenBranch, ExpressionNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            if (condition.executeBoolean(frame)) {
                return thenBranch.executeGeneric(frame);
            } else {
                return elseBranch.executeGeneric(frame);
            }
        } catch (UnexpectedResultException e) {
            throw new UnsupportedSpecializationException(this, new Node[]{condition}, e.getResult());
        }
    }

    public static IMatcher<IfNode> match(FrameDescriptor frameDescriptor) {
        return M.appl3("If", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, i, t, e) -> new IfNode(i, t, e));
    }

    public void init(InitValues initValues) {
        condition.init(initValues);
        thenBranch.init(initValues);
        elseBranch.init(initValues);
    }
}
