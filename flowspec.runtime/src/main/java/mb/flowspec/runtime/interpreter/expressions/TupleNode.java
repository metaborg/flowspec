package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Arrays;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.InitValues;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

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
        }).toArray(ITerm[]::new);
        return B.newTuple(childVals);
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
