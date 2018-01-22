package meta.flowspec.java.interpreter.patterns;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import meta.flowspec.java.interpreter.Types;

@TypeSystemReference(Types.class)
public abstract class PatternNode extends Node {
    public abstract boolean matchGeneric(VirtualFrame frame, Object value);

    public static IMatcher<PatternNode> matchPattern(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return term -> M.cases(
            // TODO Term/2
            TuplePatternNode.match(frameDescriptor, cfg),
            WildcardPatternNode.match(frameDescriptor, cfg),
            VarPatternNode.match(frameDescriptor, cfg),
            AtPatternNode.match(frameDescriptor, cfg),
            IntLiteralPatternNode.match(frameDescriptor, cfg),
            StringLiteralPatternNode.match(frameDescriptor, cfg)
            // TODO Start/0
            // TODO End/0
        ).match(term);
    }
}
