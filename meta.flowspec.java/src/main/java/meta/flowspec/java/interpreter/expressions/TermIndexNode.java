package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.locals.ReadVarNode;

@NodeChildren({@NodeChild(value = "var", type = ReadVarNode.class)})
public abstract class TermIndexNode extends ExpressionNode {
    @Specialization
    public TermIndex indexOf(ITerm term) {
        return TermIndex.get(term).get();
    }

    public static IMatcher<TermIndexNode> match(FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl1(
                "TermIndex", 
                ReadVarNode.match(frameDescriptor), 
                (appl, var) -> TermIndexNodeGen.create(var));
    }
}