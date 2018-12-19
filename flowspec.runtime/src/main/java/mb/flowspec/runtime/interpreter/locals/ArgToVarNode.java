package mb.flowspec.runtime.interpreter.locals;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import mb.flowspec.runtime.interpreter.Types;

@TypeSystemReference(Types.class)
public class ArgToVarNode extends Node {
    public ArgToVarNode(int argumentOffset, FrameSlot slot) {
        super();
        this.argumentOffset = argumentOffset;
        this.slot = slot;
    }

    private final int argumentOffset;
    private final FrameSlot slot;

    public void execute(VirtualFrame frame) {
        frame.setObject(slot, frame.getArguments()[argumentOffset]);
    }

    public static ArgToVarNode of(FrameDescriptor frameDescriptor, int offset, String name) {
        FrameSlot slot = frameDescriptor.findOrAddFrameSlot(name);
        return new ArgToVarNode(offset, slot);
    }
}
