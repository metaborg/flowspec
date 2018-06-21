package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class TermNode extends ExpressionNode {
    private final String consName;
    private final ExpressionNode[] children;

    public TermNode(String consName, ExpressionNode[] children) {
        this.consName = consName;
        this.children = children;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return B.newAppl(consName, Arrays.stream(children)
                                        .map(c -> {
                                            try {
                                                return c.executeITerm(frame);
                                            } catch (UnexpectedResultException e) {
                                                throw new RuntimeException(e);
                                            }
                                        })
                                        .collect(Collectors.toList()));
    }

    public static IMatcher<TermNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Term", M.stringValue(), M.listElems(ExpressionNode.matchExpr(frameDescriptor)),
                (appl, consName, children) -> {
                    ExpressionNode[] c = children.toArray(new ExpressionNode[children.size()]);
                    return new TermNode(consName, c);
                });
    }

    @Override
    public void init(InitValues initValues) {
        for (ExpressionNode child : children) {
            child.init(initValues);
        }
    }
}
