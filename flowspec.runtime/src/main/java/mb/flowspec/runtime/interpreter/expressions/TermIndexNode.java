package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@NodeChildren({@NodeChild(value = "var", type = ReadVarNode.class)})
public abstract class TermIndexNode extends ExpressionNode {
    protected ReadVarNode child;

    @Specialization
    public TermIndex indexOf(ITerm term) {
        return TermIndex.get(term).get();
    }

    public static IMatcher<TermIndexNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1(
                "TermIndex", 
                ReadVarNode.match(frameDescriptor), 
                (appl, var) -> {
                    TermIndexNode result = TermIndexNodeGen.create(var);
                    result.child = var;
                    return result;
                });
    }

    @Override
    public void init(InitValues initValues) {
        child.init(initValues);
    }
}
