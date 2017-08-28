package meta.flowspec.java.interpreter.expressions;

import meta.flowspec.java.interpreter.expressions.ApplicationNodeGen;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import meta.flowspec.java.interpreter.values.Function;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

@NodeChildren({@NodeChild("function"), @NodeChild("argument")})
public abstract class ApplicationNode extends ExpressionNode {
    @Specialization
    public Object execute(Function func, Object arg) {
        return func.call(arg);
    }

    public static ApplicationNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return
            ApplicationNodeGen.create(
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(0), frameDescriptor, cfg),
                ExpressionNode.fromIStrategoTerm(appl.getSubterm(1), frameDescriptor, cfg));
    }
}
