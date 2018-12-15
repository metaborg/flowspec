package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.UnreachableException;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class MapLookupNode extends ExpressionNode {
    public final ExpressionNode mapExpr;
    public final ExpressionNode keyExpr;

    public MapLookupNode(ExpressionNode mapExpr, ExpressionNode keyExpr) {
        this.mapExpr = mapExpr;
        this.keyExpr = keyExpr;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return executeITerm(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException(e);
        }
    }

    @Override
    public ITerm executeITerm(VirtualFrame frame) throws UnexpectedResultException {
        io.usethesource.capsule.Map.Immutable<ITerm, ITerm> map = mapExpr.executeIMap(frame).getMap();
        ITerm key = keyExpr.executeITerm(frame);
        return map.get(key);
    }

    public static IMatcher<MapLookupNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("MapLookup", 
                ExpressionNode.matchExpr(frameDescriptor),
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, mapExpr, keyExpr) -> new MapLookupNode(mapExpr, keyExpr));
    }

    public void init(InitValues initValues) {
        mapExpr.init(initValues);
        keyExpr.init(initValues);
    }
}
