package meta.flowspec.java.interpreter.patterns;

import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import meta.flowspec.java.interpreter.InitValues;
import meta.flowspec.java.interpreter.Types;

@TypeSystemReference(Types.class)
public abstract class PatternNode extends Node {
    public abstract boolean matchGeneric(VirtualFrame frame, Object value);

    public static IMatcher<PatternNode> matchPattern(FrameDescriptor frameDescriptor) {
        return term -> M.cases(
            // TODO Term/2
            TuplePatternNode.match(frameDescriptor),
            WildcardPatternNode.match(frameDescriptor),
            VarPatternNode.match(frameDescriptor),
            AtPatternNode.match(frameDescriptor),
            IntLiteralPatternNode.match(frameDescriptor),
            StringLiteralPatternNode.match(frameDescriptor)
            // TODO Start/0
            // TODO End/0
        ).match(term);
    }

    public abstract void init(InitValues initValues);
}
