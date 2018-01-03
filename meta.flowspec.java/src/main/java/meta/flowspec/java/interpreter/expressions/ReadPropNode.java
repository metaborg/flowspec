package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.locals.ReadVarNode;

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

    public static IMatcher<ReadPropNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl2("Prop", 
                M.stringValue(),
                ReadVarNode.match(frameDescriptor), 
                (appl, propName, rhs) -> new ReadPropNode(cfg, propName, rhs));
    }
}
