package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.Map;
import mb.flowspec.terms.M;

public class MapLiteralNode extends ExpressionNode {
    private final ExpressionNode[] values;

    public MapLiteralNode(ExpressionNode[] values) {
        this.values = values;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        return executeIMap(frame);
    }

    @Override public IMap<IStrategoTerm, IStrategoTerm> executeIMap(VirtualFrame frame) {
        io.usethesource.capsule.Map.Transient<IStrategoTerm, IStrategoTerm> map =
            io.usethesource.capsule.Map.Transient.of();
        for(ExpressionNode expr : values) {
            try {
                IStrategoTuple tuple = M.tuple(expr.executeIStrategoTerm(frame), 2);
                map.__put(M.at(tuple, 0), M.at(tuple, 1));
            } catch(UnexpectedResultException e) {
                throw new RuntimeException(e);
            }
        }
        return new Map<>(map.freeze());
    }
}
