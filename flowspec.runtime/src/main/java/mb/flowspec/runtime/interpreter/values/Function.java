package mb.flowspec.runtime.interpreter.values;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.MaterializedFrame;

public class Function {
    private final CallTarget callTarget;
    private final MaterializedFrame frame;
    
    public Function(CallTarget callTarget, MaterializedFrame frame) {
        this.callTarget = callTarget;
        this.frame = frame;
    }
    
    public Object call(Object argument) {
        return this.callTarget.call(frame, argument);
    }
}
