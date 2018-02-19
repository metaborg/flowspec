package meta.flowspec.java.interpreter.patterns;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import meta.flowspec.java.interpreter.Types;

@TypeSystemReference(Types.class)
public abstract class PatternNode extends Node {
    public abstract boolean matchGeneric(VirtualFrame frame, Object value);

    public static IMatcher<PatternNode> matchPattern(FrameDescriptor frameDescriptor) {
        return (term, unifier) -> M.cases(
            // TODO Term/2
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

    public void init(ISolution solution) {
        // Do nothing
    }
}
