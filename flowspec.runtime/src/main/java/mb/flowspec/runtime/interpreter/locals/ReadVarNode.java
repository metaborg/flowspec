package mb.flowspec.runtime.interpreter.locals;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.runtime.interpreter.expressions.RefNode;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class ReadVarNode extends RefNode {
    protected abstract FrameSlot getSlot();

    @Specialization(guards = "isInt(frame)")
    protected int readInt(VirtualFrame frame) {
        try {
            return frame.getInt(getSlot());
        } catch (FrameSlotTypeException e) {
            return Types.asInteger(FrameUtil.getObjectSafe(frame, getSlot()));
        }
    }

    @Specialization(guards = "isBoolean(frame)")
    protected boolean readBoolean(VirtualFrame frame) {
        try {
            return frame.getBoolean(getSlot());
        } catch (FrameSlotTypeException e) {
            return Types.asBoolean(FrameUtil.getObjectSafe(frame, getSlot()));
        }
    }

    @Specialization(replaces = {"readInt", "readBoolean"})
    protected Object readObject(VirtualFrame frame) {
        return Types.typeSpecializeTerm(FrameUtil.getObjectSafe(frame, getSlot()));
    }

    protected boolean isInt(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Int || getSlot().getKind() == FrameSlotKind.Object
                && Types.isInteger(FrameUtil.getObjectSafe(frame, getSlot()));
    }

    protected boolean isBoolean(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Boolean || getSlot().getKind() == FrameSlotKind.Object
                && Types.isBoolean(FrameUtil.getObjectSafe(frame, getSlot()));
    }
}