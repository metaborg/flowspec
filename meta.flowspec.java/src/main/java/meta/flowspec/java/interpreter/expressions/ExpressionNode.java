package meta.flowspec.java.interpreter.expressions;

import meta.flowspec.java.interpreter.TypesGen;
import meta.flowspec.java.interpreter.locals.ReadVarNodeGen;
import meta.flowspec.java.interpreter.values.Tuple;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

import io.usethesource.capsule.Set;

import org.metaborg.meta.nabl2.terms.ITerm;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.Types;

@TypeSystemReference(Types.class)
public abstract class ExpressionNode extends Node {
    /**
     * The execute method when no specialisation is possible. This is the most general case,
     * therefore it must be provided by all subclasses.
     */
    public abstract Object executeGeneric(VirtualFrame frame);

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectInteger(executeGeneric(frame));
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectBoolean(executeGeneric(frame));
    }
    
    public Tuple executeTuple(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectTuple(executeGeneric(frame));
    }
    
    @SuppressWarnings("unchecked")
    public meta.flowspec.java.interpreter.Set<ITerm> executeSet(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectSet(executeGeneric(frame));
    }

    public static ExpressionNode fromIStrategoTerm(IStrategoTerm term, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        assert term instanceof IStrategoAppl : "Expected a constructor application term";
        final IStrategoAppl appl = (IStrategoAppl) term;
        switch (appl.getConstructor().getName()) {
            case "Term": throw new RuntimeException("Unimplemented");
            case "QualRef": throw new RuntimeException("Unimplemented");
            case "Ref": {
                assert appl.getSubtermCount() == 1 : "Expected Ref to have 1 child";
                return ReadVarNodeGen.create(frameDescriptor.findFrameSlot(Tools.javaStringAt(appl, 0)));
            }
            case "Prop": {
                assert appl.getSubtermCount() == 2 : "Expected PropRef to have 2 children";
                return ReadPropNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "Tuple": {
                assert appl.getSubtermCount() == 2 : "Expected Tuple to have 2 children";
                return TupleNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "Int": {
                assert appl.getSubtermCount() == 1 : "Expected Int to have 1 child";
                return IntLiteralNode.fromIStrategoAppl(appl);
            }
            case "String": {
                assert appl.getSubtermCount() == 1 : "Expected String to have 1 child";
                return StringLiteralNode.fromIStrategoAppl(appl);
            }
            case "Type": {
                assert appl.getSubtermCount() == 1 : "Expected Type to have 1 child";
                return new TypeNode(ReadVarNodeGen.create(frameDescriptor.findFrameSlot(Tools.javaStringAt(appl, 0))));
            }
            case "Abs": throw new RuntimeException("Unimplemented");
            case "Appl": throw new RuntimeException("Unimplemented");
            case "If": {
                assert appl.getSubtermCount() == 3 : "Expected If to have 3 children";
                return IfNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "Eq": {
                assert appl.getSubtermCount() == 2 : "Expected Eq to have 2 children";
                return EqualNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "NEq": {
                assert appl.getSubtermCount() == 2 : "Expected NEq to have 2 children";
                return NotEqualNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "Plus": {
                assert appl.getSubtermCount() == 2 : "Expected Plus to have 2 children";
                return PlusNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "Match": throw new RuntimeException("Unimplemented");
            case "SetLiteral": {
                assert appl.getSubtermCount() == 1 : "Expected SetLiteral to have 1 child";
                return SetLiteralNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "SetComp": {
                assert appl.getSubtermCount() == 4 : "Expected SetComp to have 4 children";
                return SetCompNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "SetUnion": {
                assert appl.getSubtermCount() == 2 : "Expected SetUnion to have 2 children";
                return SetUnionNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "SetDifference": {
                assert appl.getSubtermCount() == 2 : "Expected SetDifference to have 2 children";
                return SetMinusNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "SetContains": {
                assert appl.getSubtermCount() == 2 : "Expected SetContains to have 2 children";
                return SetContainsNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "SetIntersection": {
                assert appl.getSubtermCount() == 2 : "Expected SetIntersection to have 2 children";
                return SetIntersectNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            default: throw new IllegalArgumentException("Unknown constructor for Expression: " + appl.getConstructor().getName());
        }
    }

    public static class Array {
        public static ExpressionNode[] fromIStrategoTerm(IStrategoTerm term, FrameDescriptor frameDescriptor,
                IControlFlowGraph<ICFGNode> cfg) {
            assert term instanceof IStrategoList : "Expected a list term";
            final IStrategoList list = (IStrategoList) term;
            ExpressionNode[] result = new ExpressionNode[term.getSubtermCount()];
            int i = 0;
            for (IStrategoTerm sourceTerm : list) {
                result[i] = ExpressionNode.fromIStrategoTerm(sourceTerm, frameDescriptor, cfg);
                i++;
            }
            return result;
        }
    }
}
