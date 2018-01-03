package meta.flowspec.java.interpreter.expressions;

import java.util.Arrays;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.values.Tuple;

public class TupleNode extends ExpressionNode {
    @Children
    private final ExpressionNode[] children;

    public TupleNode(ExpressionNode[] children) {
        super();
        this.children = children;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] childVals = Arrays.stream(children).map(c -> c.executeGeneric(frame)).toArray();
        return new Tuple(childVals);
    }

    public static IMatcher<TupleNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl2("Tuple", 
                ExpressionNode.matchExpr(frameDescriptor, cfg),
                M.listElems(ExpressionNode.matchExpr(frameDescriptor, cfg)), 
                (appl, first, others) -> {
                    ExpressionNode[] exprs = new ExpressionNode[others.size() + 1];
                    int i = 0;
                    exprs[i] = first;
                    for(ExpressionNode expr : others) {
                        i++;
                        exprs[i] = expr;
                    }
                    return new TupleNode(exprs);
                });
    }
}
