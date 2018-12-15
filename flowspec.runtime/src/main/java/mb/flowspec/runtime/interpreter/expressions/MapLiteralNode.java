package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.Map;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public class MapLiteralNode extends ExpressionNode {
    private final ExpressionNode[] values;
    
    public MapLiteralNode(ExpressionNode[] values) {
        this.values = values;
    }
    
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeIMap(frame);
    }

    @Override
    public IMap<ITerm, ITerm> executeIMap(VirtualFrame frame) {
        io.usethesource.capsule.Map.Transient<ITerm, ITerm> map = io.usethesource.capsule.Map.Transient.of();
        for (ExpressionNode expr : values) {
            try {
                ITerm term = expr.executeITerm(frame);
                Tuple2<ITerm, ITerm> tuple = M.tuple2(M.term(), M.term()).match(term)
                        .map(t -> ImmutableTuple2.of(t.getArgs().get(0), t.getArgs().get(1)))
                        .orElseThrow(() -> new UnexpectedResultException(term));
                map.__put(tuple._1(), tuple._2());
            } catch (UnexpectedResultException e) {
                throw new RuntimeException(e);
            }
        }
        return new Map<>(map.freeze());
    }

    public static IMatcher<MapLiteralNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("MapLiteral", 
                M.listElems(ExpressionNode.matchExpr(frameDescriptor)),
                (appl, exprs) -> new MapLiteralNode(exprs.toArray(new ExpressionNode[exprs.size()])));
    }

    public void init(InitValues initValues) {
        for (ExpressionNode value : values) {
            value.init(initValues);
        }
    }
}
