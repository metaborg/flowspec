package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@NodeChildren({@NodeChild("number")})
public abstract class NegNode extends ExpressionNode {
    protected ExpressionNode child;

    @Specialization
    protected int negate(int number) {
        return -number;
    }

    public static IMatcher<NegNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("Neg", 
                ExpressionNode.matchExpr(frameDescriptor), 
                (appl, e) -> {
                    NegNode result = NegNodeGen.create(e);
                    result.child = e;
                    return result;
                });
    }

    public void init(InitValues initValues) {
        child.init(initValues);
    }
}
