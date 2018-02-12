package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

@NodeChildren({@NodeChild("expr")})
public abstract class NotNode extends ExpressionNode {
    @Specialization
    protected boolean plus(boolean expr) {
        return !expr;
    }

    public static IMatcher<NotNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl1("Not",  
                ExpressionNode.matchExpr(frameDescriptor, solution),
                (appl, e) -> NotNodeGen.create(e));
    }
}
