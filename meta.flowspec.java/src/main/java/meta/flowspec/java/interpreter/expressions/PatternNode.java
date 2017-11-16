package meta.flowspec.java.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import meta.flowspec.java.interpreter.Types;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

@TypeSystemReference(Types.class)
public abstract class PatternNode extends Node {
    public static PatternNode fromIStrategoTerm(IStrategoTerm term, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        assert term instanceof IStrategoAppl : "Expected a constructor application term";
        final IStrategoAppl appl = (IStrategoAppl) term;
        switch (appl.getConstructor().getName()) {
            case "Term": throw new RuntimeException("Unimplemented");
            case "Tuple": throw new RuntimeException("Unimplemented");
            case "Wildcard": throw new RuntimeException("Unimplemented");
            case "Var": {
                assert appl.getSubtermCount() == 1 : "Expected Ref to have 1 child";
                return VarPatternNode.fromIStrategoAppl(appl, frameDescriptor, cfg);
            }
            case "At": throw new RuntimeException("Unimplemented");
            case "Int": {
                assert appl.getSubtermCount() == 1 : "Expected Int to have 1 child";
                return IntLiteralPatternNode.fromIStrategoAppl(appl);
            }
            case "String": {
                assert appl.getSubtermCount() == 1 : "Expected String to have 1 child";
                return StringLiteralPatternNode.fromIStrategoAppl(appl);
            }
            case "Start": throw new RuntimeException("Unimplemented");
            case "End": throw new RuntimeException("Unimplemented");
            default: throw new IllegalArgumentException("Unknown constructor for Expression: " + appl.getConstructor().getName());
        }
    }

    public static class Array {
        public static PatternNode[] fromIStrategoTerm(IStrategoTerm term, FrameDescriptor frameDescriptor,
                IControlFlowGraph<ICFGNode> cfg) {
            assert term instanceof IStrategoList : "Expected a list term";
            final IStrategoList list = (IStrategoList) term;
            PatternNode[] result = new PatternNode[term.getSubtermCount()];
            int i = 0;
            for (IStrategoTerm sourceTerm : list) {
                result[i] = PatternNode.fromIStrategoTerm(sourceTerm, frameDescriptor, cfg);
                i++;
            }
            return result;
        }
    }

    public abstract boolean executeGeneric(VirtualFrame frame, Object value);
}
