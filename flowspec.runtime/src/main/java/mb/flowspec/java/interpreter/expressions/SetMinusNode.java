package mb.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.java.interpreter.InitValues;
import mb.flowspec.java.interpreter.SymbolicLargestSetException;
import mb.flowspec.java.interpreter.values.Set;
import meta.flowspec.java.interpreter.expressions.SetMinusNodeGen;

@NodeChildren({@NodeChild("left"), @NodeChild("right")})
public abstract class SetMinusNode extends ExpressionNode {
    protected ExpressionNode[] children;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Specialization
    protected Set minus(Set left, Set right) {
        if (left.set == null) { // handle symbolic value of set with everything in it
            throw new SymbolicLargestSetException();
        }
        if (right.set == null) { // handle symbolic value of set with everything in it
            return new Set(io.usethesource.capsule.Set.Immutable.of());
        }
        return new Set(io.usethesource.capsule.Set.Immutable.subtract(left.set, right.set));
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
