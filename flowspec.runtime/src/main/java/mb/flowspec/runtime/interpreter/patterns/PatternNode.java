package mb.flowspec.runtime.interpreter.patterns;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.Types;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@TypeSystemReference(Types.class)
public abstract class PatternNode extends Node {
    public abstract boolean matchGeneric(VirtualFrame frame, Object value);

    public static IMatcher<PatternNode> matchPattern(FrameDescriptor frameDescriptor) {
        return (term, unifier) -> M.cases(
            ConsPatternNode.match(frameDescriptor),
            NilPatternNode.match(frameDescriptor),
            TermPatternNode.match(frameDescriptor),
            TuplePatternNode.match(frameDescriptor),
            WildcardPatternNode.match(frameDescriptor),
            VarPatternNode.match(frameDescriptor),
            AtPatternNode.match(frameDescriptor),
            IntLiteralPatternNode.match(frameDescriptor),
            StringLiteralPatternNode.match(frameDescriptor)
            // TODO Start/0
            // TODO End/0
        ).match(term, unifier);
    }

    public abstract void init(InitValues initValues);
}
