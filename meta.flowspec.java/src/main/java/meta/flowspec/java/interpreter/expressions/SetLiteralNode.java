package meta.flowspec.java.interpreter.expressions;

import io.usethesource.capsule.Set;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SetLiteralNode extends ExpressionNode {
    private final Set.Immutable<Object> value;
    
    public SetLiteralNode(Set.Immutable<Object> value) {
        this.value = value;
    }
    
    @Override
    public Object executeGeneric(VirtualFrame _frame) {
        return value;
    }

    @Override
    public Set.Immutable<Object> executeSet(VirtualFrame _frame) {
        return value;
    }

}
