package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.flowspec.terms.TermIndex;

@NodeChildren({ @NodeChild(value = "var", type = ReadVarNode.class) })
public abstract class TermIndexNode extends ExpressionNode {
    @Specialization public TermIndex indexOf(IStrategoTerm term) {
        return TermIndex.get(term).get();
    }
}
