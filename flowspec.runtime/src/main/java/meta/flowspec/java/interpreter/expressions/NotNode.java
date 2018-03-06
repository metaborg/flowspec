package meta.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.InitValues;

@NodeChildren({@NodeChild("expr")})
public abstract class NotNode extends ExpressionNode {
    protected ExpressionNode _expr;
    
    @Specialization
    protected boolean plus(boolean expr) {
        return !expr;
    }

    public static IMatcher<NotNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("Not",  
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, e) -> {
                    NotNode result = NotNodeGen.create(e);
                    result._expr = e;
                    return result;
                });
    }

    public void init(InitValues initValues) {
        this._expr.init(initValues);
    }
}
