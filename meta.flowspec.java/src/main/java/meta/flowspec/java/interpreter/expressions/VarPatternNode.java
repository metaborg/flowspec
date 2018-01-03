package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

public class VarPatternNode extends PatternNode {
    public final FrameSlot slot;

    public VarPatternNode(FrameSlot slot) {
        this.slot = slot;
    }

    public static VarPatternNode fromIStrategoAppl(IStrategoAppl appl, FrameDescriptor frameDescriptor,
            IControlFlowGraph<ICFGNode> cfg) {
        FrameSlotKind slotKind = FrameSlotKind.Illegal; // TODO: getType(appl)
        FrameSlot slot = frameDescriptor.addFrameSlot(Tools.javaStringAt(appl, 0), slotKind);
        return new VarPatternNode(slot);
    }

    @Override
    public boolean executeGeneric(VirtualFrame frame, Object value) {
        this.slot.setKind(FrameSlotKind.Object);
        frame.setObject(this.slot, value);
        return true;
    }

    public static IMatcher<VarPatternNode> match(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl1("Var", M.stringValue(), (appl, name) -> {
            FrameSlotKind slotKind = FrameSlotKind.Illegal; // TODO: getType(appl)
            return new VarPatternNode(frameDescriptor.addFrameSlot(name, slotKind));
        });
    }

}
