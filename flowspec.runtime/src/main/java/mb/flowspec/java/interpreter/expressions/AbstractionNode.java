package mb.flowspec.java.interpreter.expressions;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.java.interpreter.FlowSpecRootNode;
import mb.flowspec.java.interpreter.InitValues;
import mb.flowspec.java.interpreter.values.Function;


public class AbstractionNode extends ExpressionNode {
    private final FlowSpecRootNode rootNode;
    
    public AbstractionNode(FlowSpecRootNode rootNode) {
        this.rootNode = rootNode;
    }
    
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new Function(Truffle.getRuntime().createCallTarget(rootNode), frame.materialize());
    }

    @Override
    public void init(InitValues initValues) {
        // TODO Auto-generated method stub
        
    }

//    public static AbstractionNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor, IControlFlowGraph<CFGNode> cfg) {
//        IStrategoTerm[] params = Tools.listAt(appl, 0).getAllSubterms();
//        String[] patternVars = Arrays.stream(params).map(Tools::javaString).toArray(String[]::new);
//        
//        ArgToVarNode[] patternVariables = new ArgToVarNode[patternVars.length];
//        for (int i = 0; i < patternVars.length; i++) {
//            FrameSlot slot = frameDescriptor.addFrameSlot(patternVars[i], FrameSlotKind.Object);
//            patternVariables[i] = new ArgToVarNode(i, slot);
//        }
//        // TODO: Is it ok to use the same frameDescriptor here?
//        ExpressionNode body = ExpressionNode.fromIStrategoTerm(appl.getSubterm(1), frameDescriptor, cfg);
//        
//        return new AbstractionNode(new FlowSpecRootNode(patternVariables, body, frameDescriptor));
//    }
}
