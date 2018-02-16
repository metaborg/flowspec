package meta.flowspec.java.interpreter.expressions;

import java.util.Arrays;

import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.terms.generic.TB;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.InitValues;

public class TupleNode extends ExpressionNode {
    @Children
    private final ExpressionNode[] children;

    public TupleNode(ExpressionNode[] children) {
        super();
        this.children = children;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeITerm(frame);
    }

    @Override
    public ITerm executeITerm(VirtualFrame frame) {
        ITerm[] childVals = Arrays.stream(children).map(c -> { // Java streams and exceptions... smh
            try {
                return c.executeITerm(frame);
            } catch (UnexpectedResultException e) {
                throw new TypeErrorException(e);
            }
        }).toArray(i -> new ITerm[i]);
        return TB.newTuple(childVals);
    }

    public static IMatcher<TupleNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Tuple", 
                ExpressionNode.matchExpr(frameDescriptor),
                M.listElems(ExpressionNode.matchExpr(frameDescriptor)), 
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

    @Override
    public void init(InitValues initValues) {
        for (ExpressionNode child : children) {
            child.init(initValues);
        }
    }
}
