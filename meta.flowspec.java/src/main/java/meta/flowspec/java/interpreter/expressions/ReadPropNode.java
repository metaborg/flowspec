package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.locals.ReadVarNode;
import meta.flowspec.java.interpreter.locals.ReadVarNodeGen;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

public class ReadPropNode extends ExpressionNode {
    private IControlFlowGraph<ICFGNode> cfg;
    private final String propName;

    @Child
    private ReadVarNode rhs;

    public ReadPropNode(IControlFlowGraph<ICFGNode> cfg, String propName, ReadVarNode rhs) {
        this.cfg = cfg;
        this.propName = propName;
        this.rhs = rhs;
    }

    // TODO: write a specialisation instead based on the rhs @Child field?
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return cfg.getProperty((ICFGNode) rhs.executeGeneric(frame), propName);
    }

    public static ReadPropNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        String propName = Tools.javaStringAt(appl, 0);
        ReadVarNode rhs = ReadVarNodeGen.create(frameDescriptor.findFrameSlot(Tools.javaStringAt(appl, 1)));
        return new ReadPropNode(cfg, propName, rhs);
    }
}
