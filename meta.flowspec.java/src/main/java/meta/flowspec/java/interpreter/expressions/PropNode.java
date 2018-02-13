package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.locals.ReadVarNode;

public class PropNode extends ExpressionNode {
    private IControlFlowGraph<CFGNode> cfg;
    private final String propName;

    @Child
    private ReadVarNode rhs;

    public PropNode(String propName, ReadVarNode rhs) {
        this.propName = propName;
        this.rhs = rhs;
    }

    // TODO: write a specialisation instead based on the rhs @Child field?
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return cfg.getProperty((CFGNode) rhs.executeGeneric(frame), propName);
    }

    public static IMatcher<PropNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Prop", 
                M.stringValue(),
                ReadVarNode.match(frameDescriptor), 
                (appl, propName, rhs) -> new PropNode(propName, rhs));
    }

    @Override
    public void init(ISolution solution) {
        this.cfg = solution.controlFlowGraph();
    }
}
