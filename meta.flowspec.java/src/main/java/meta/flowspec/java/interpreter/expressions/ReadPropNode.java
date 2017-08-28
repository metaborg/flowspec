package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

public class ReadPropNode extends ExpressionNode {
    private IControlFlowGraph<ICFGNode> cfg;
    private final String propName;

    @Child
    private ExpressionNode rhs;

    public ReadPropNode(IControlFlowGraph<ICFGNode> cfg, String propName, ExpressionNode rhs) {
        this.cfg = cfg;
        this.propName = propName;
        this.rhs = rhs;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return cfg.getProperty((ICFGNode) rhs.executeGeneric(frame), propName);
    }

    public static ReadPropNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        String propName = Tools.javaStringAt(appl, 0);
        ExpressionNode rhs = ExpressionNode.fromIStrategoTerm(appl.getSubterm(1), frameDescriptor, cfg);
        return new ReadPropNode(cfg, propName, rhs);
    }
}
