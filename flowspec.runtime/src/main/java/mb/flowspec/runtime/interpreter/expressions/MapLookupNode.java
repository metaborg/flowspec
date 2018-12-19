package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.UnreachableException;

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
            return executeIStrategoTerm(frame);
        } catch (UnexpectedResultException e) {
            throw new UnreachableException(e);
        }
    }

    @Override
    public IStrategoTerm executeIStrategoTerm(VirtualFrame frame) throws UnexpectedResultException {
        io.usethesource.capsule.Map.Immutable<IStrategoTerm, IStrategoTerm> map = mapExpr.executeIMap(frame).getMap();
        IStrategoTerm key = keyExpr.executeIStrategoTerm(frame);
        return map.get(key);
    }
}
