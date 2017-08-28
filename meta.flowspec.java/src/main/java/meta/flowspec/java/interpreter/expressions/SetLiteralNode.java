package meta.flowspec.java.interpreter.expressions;

import org.pcollections.PSet;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SetLiteralNode extends ExpressionNode {
    private final PSet<Object> value;
    
    public SetLiteralNode(PSet<Object> value) {
        this.value = value;
    }
    
    @Override
    public Object executeGeneric(VirtualFrame _frame) {
        return value;
    }

    @Override
    public PSet<Object> executeSet(VirtualFrame _frame) {
        return value;
    }

}
