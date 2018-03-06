package mb.flowspec.runtime.interpreter.patterns;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IBasicControlFlowGraph;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.InitValues;

public class VarPatternNode extends PatternNode {
    public final FrameSlot slot;

    public VarPatternNode(FrameSlot slot) {
        this.slot = slot;
    }

    public static VarPatternNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor,
            IBasicControlFlowGraph<CFGNode> cfg) {
        FrameSlotKind slotKind = FrameSlotKind.Illegal; // TODO: getType(appl)
        FrameSlot slot = frameDescriptor.addFrameSlot(Tools.javaStringAt(appl, 0), slotKind);
        return new VarPatternNode(slot);
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
