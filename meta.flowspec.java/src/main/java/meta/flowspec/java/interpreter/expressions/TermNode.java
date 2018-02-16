package meta.flowspec.java.interpreter.expressions;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.terms.generic.TB;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.InitValues;

public class TermNode extends ExpressionNode {
    private final String consName;
    private final ExpressionNode[] children;

    public TermNode(String consName, ExpressionNode[] children) {
        this.consName = consName;
        this.children = children;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return TB.newAppl(consName, Arrays.stream(children)
                                        .map(c -> (ITerm) c.executeGeneric(frame))
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
