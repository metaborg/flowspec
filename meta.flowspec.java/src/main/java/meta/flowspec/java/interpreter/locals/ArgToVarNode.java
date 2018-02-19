package meta.flowspec.java.interpreter.locals;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import meta.flowspec.java.interpreter.Types;

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
    
    public static IMatcher<ArgToVarNode[]> matchList(FrameDescriptor frameDescriptor) {
        return M.listElems(M.stringValue()).map(patternVars -> {
            ArgToVarNode[] patternVariables = new ArgToVarNode[patternVars.size()];
            for (int i = 0; i < patternVars.size(); i++) {
                FrameSlot slot = frameDescriptor.addFrameSlot(patternVars.get(i), FrameSlotKind.Object);
                patternVariables[i] = new ArgToVarNode(i, slot);
            }
            return patternVariables;
        });
    }
}
