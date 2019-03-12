package mb.flowspec.runtime.interpreter.patterns;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import mb.flowspec.runtime.interpreter.Types;

@TypeSystemReference(Types.class)
public abstract class PatternNode extends Node {
    public abstract boolean matchGeneric(VirtualFrame frame, Object value);
}
