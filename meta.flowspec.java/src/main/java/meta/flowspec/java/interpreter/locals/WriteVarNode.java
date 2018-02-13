package meta.flowspec.java.interpreter.locals;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import meta.flowspec.java.interpreter.Types;
import meta.flowspec.java.interpreter.expressions.ExpressionNode;

@TypeSystemReference(Types.class)
@NodeChild(value = "valNode", type = ExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class WriteVarNode extends Node {

    protected abstract FrameSlot getSlot();

    public abstract void execute(VirtualFrame frame);

    protected ExpressionNode expr;

    @Specialization(guards = "isInt(frame)")
    protected void writeInt(VirtualFrame frame, int value) {
        getSlot().setKind(FrameSlotKind.Int);

        frame.setInt(getSlot(), value);
    }

//    @Specialization(guards = "isLong(frame)")
//    protected void writeLong(VirtualFrame frame, long value) {
//        getSlot().setKind(FrameSlotKind.Long);
//
//        frame.setLong(getSlot(), value);
//    }
//
//    @Specialization(guards = "isFloat(frame)")
//    protected void writeFloat(VirtualFrame frame, float value) {
//        getSlot().setKind(FrameSlotKind.Float);
//
//        frame.setFloat(getSlot(), value);
//    }
//
//    @Specialization(guards = "isDouble(frame)")
//    protected void writeDouble(VirtualFrame frame, double value) {
//        getSlot().setKind(FrameSlotKind.Double);
//
//        frame.setDouble(getSlot(), value);
//    }

    @Specialization(guards = "isBoolean(frame)")
    protected void writeBoolean(VirtualFrame frame, boolean value) {
        getSlot().setKind(FrameSlotKind.Boolean);

        frame.setBoolean(getSlot(), value);
    }

    @Specialization(replaces = { "writeInt", /*"writeLong", "writeFloat", "writeDouble",*/ "writeBoolean" })
    protected void writeObject(VirtualFrame frame, Object value) {

        getSlot().setKind(FrameSlotKind.Object);

        frame.setObject(getSlot(), value);
    }

    protected boolean isInt(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Int || getSlot().getKind() == FrameSlotKind.Illegal;
    }

//    protected boolean isLong(VirtualFrame frame) {
//        return getSlot().getKind() == FrameSlotKind.Long || getSlot().getKind() == FrameSlotKind.Illegal;
//    }
//
//    protected boolean isFloat(VirtualFrame frame) {
//        return getSlot().getKind() == FrameSlotKind.Float || getSlot().getKind() == FrameSlotKind.Illegal;
//    }
//
//    protected boolean isDouble(VirtualFrame frame) {
//        return getSlot().getKind() == FrameSlotKind.Double || getSlot().getKind() == FrameSlotKind.Illegal;
//    }

    protected boolean isBoolean(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Boolean || getSlot().getKind() == FrameSlotKind.Illegal;
    }

    public void init(ISolution solution) {
        this.expr.init(solution);
    }

    public static IMatcher<WriteVarNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Binding", M.stringValue(), ExpressionNode.matchExpr(frameDescriptor), (appl, name, expr) -> {
            // TODO: getType(appl)
            FrameSlotKind slotKind = FrameSlotKind.Illegal;
            WriteVarNode result = WriteVarNodeGen.create(expr, frameDescriptor.addFrameSlot(name, slotKind));
            result.expr = expr;
            return result;
        });
    }

}