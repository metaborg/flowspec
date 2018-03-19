package mb.flowspec.runtime.interpreter.locals;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Objects;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.expressions.RefNode;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class ReadVarNode extends RefNode {
    protected abstract FrameSlot getSlot();

    @Specialization(guards = "isInt(frame)")
    protected int readInt(VirtualFrame frame) {
        return FrameUtil.getIntSafe(frame, getSlot());
    }

    @Specialization(guards = "isBoolean(frame)")
    protected boolean readBoolean(VirtualFrame frame) {
        return FrameUtil.getBooleanSafe(frame, getSlot());
    }

    @Specialization(replaces = {"readInt", "readBoolean"})
    protected Object readObject(VirtualFrame frame) {
        return FrameUtil.getObjectSafe(frame, getSlot());
    }

    protected boolean isInt(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Int;
    }

    protected boolean isBoolean(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Boolean;
    }
    
    public static IMatcher<ReadVarNode> match(FrameDescriptor frameDescriptor) {
        return M.stringValue().map(string -> ReadVarNodeGen.create(Objects.requireNonNull(frameDescriptor.findFrameSlot(string))));
    }

    @Override
    public void init(InitValues initValues) {
        // Do nothing
    }
}