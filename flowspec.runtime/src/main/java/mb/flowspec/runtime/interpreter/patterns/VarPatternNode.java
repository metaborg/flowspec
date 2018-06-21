package mb.flowspec.runtime.interpreter.patterns;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class VarPatternNode extends PatternNode {
    public final FrameSlot slot;

    public VarPatternNode(FrameSlot slot) {
        this.slot = slot;
    }

    @Override
    public boolean matchGeneric(VirtualFrame frame, Object value) {
        this.slot.setKind(FrameSlotKind.Object);
        frame.setObject(this.slot, value);
        return true;
    }

    public static IMatcher<VarPatternNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1("Var", M.stringValue(), (appl, name) -> {
            FrameSlotKind slotKind = FrameSlotKind.Illegal; // TODO: getType(appl)
            return new VarPatternNode(frameDescriptor.addFrameSlot(name, slotKind));
        });
    }

    @Override
    public void init(InitValues initValues) {
        // do nothing
    }
}
