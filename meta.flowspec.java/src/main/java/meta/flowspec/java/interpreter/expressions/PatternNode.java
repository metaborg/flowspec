package meta.flowspec.java.interpreter.expressions;

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
    public abstract boolean executeGeneric(VirtualFrame frame, Object value);

    public static IMatcher<PatternNode> matchPattern(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.cases(
            // TODO Term/0?
            // TODO Tuple/2?
            // TODO Wildcard/0
            VarPatternNode.match(frameDescriptor, cfg),
            // TODO At/2
            IntLiteralPatternNode.match(frameDescriptor, cfg),
            StringLiteralPatternNode.match(frameDescriptor, cfg)
            // TODO Start/0
            // TODO End/0
        );
    }
}
