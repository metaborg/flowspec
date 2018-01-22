package meta.flowspec.java.interpreter.expressions;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.terms.generic.TB;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

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

    public static IMatcher<TermNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl2("Term", M.stringValue(), M.listElems(ExpressionNode.matchExpr(frameDescriptor, cfg)),
                (appl, consName, children) -> {
                    ExpressionNode[] c = children.toArray(new ExpressionNode[children.size()]);
                    return new TermNode(consName, c);
                });
    }
}
