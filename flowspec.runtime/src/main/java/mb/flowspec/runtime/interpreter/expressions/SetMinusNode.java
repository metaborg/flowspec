package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.SymbolicLargestSetException;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Set;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetMinusNode extends ExpressionNode {
    protected ExpressionNode[] children;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected ISet minus(ISet left, ISet right) {
        // handle symbolic value of set with everything in it
        try {
            right.getSet();
        } catch(SymbolicLargestSetException e) {
            return new Set();
        }
        return new Set(io.usethesource.capsule.Set.Immutable.subtract(left.getSet(), right.getSet()));
    }

    public static IMatcher<SetMinusNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("SetDifference", 
                ExpressionNode.matchExpr(frameDescriptor), 
                ExpressionNode.matchExpr(frameDescriptor),
                (appl, e1, e2) -> {
                    SetMinusNode result = SetMinusNodeGen.create(e1, e2);
                    result.children = new ExpressionNode[] {e1,e2};
                    return result;
                });
    }
    
    public void init(InitValues initValues) {
        for (ExpressionNode child : children) {
            child.init(initValues);
        }
    }
}
