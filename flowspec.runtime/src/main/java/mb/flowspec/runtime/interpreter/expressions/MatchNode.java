package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Arrays;
import java.util.Optional;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.patterns.PatternNode;
import mb.flowspec.runtime.solver.ParseException;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;

public class MatchNode extends ExpressionNode {
    @Child
    private ExpressionNode subject;
    @Children
    private PatternNode[] matchArms;
    @Children
    private ExpressionNode[] matchBodies;

    public MatchNode(ExpressionNode subject, PatternNode[] matchArms, ExpressionNode[] matchBodies) {
        this.subject = subject;
        this.matchArms = matchArms;
        this.matchBodies = matchBodies;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object value = subject.executeGeneric(frame);
        for (int i = 0; i < matchArms.length; i++) {
            PatternNode p = matchArms[i];
            if (p.matchGeneric(frame, value)) {
                return matchBodies[i].executeGeneric(frame);
            }
        }
        return null;
    }

    @Override
    public void init(InitValues initValues) {
        subject.init(initValues);
        Arrays.stream(matchArms).forEach(pn -> pn.init(initValues));
        Arrays.stream(matchBodies).forEach(en -> en.init(initValues));
    }

    public static IMatcher<MatchNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Match", ExpressionNode.matchExpr(frameDescriptor), M.listElems(matchArm(frameDescriptor)),
                (appl, e, arms) -> new MatchNode(e, arms.stream().map(ImmutableTuple2::_1).toArray(PatternNode[]::new),
                        arms.stream().map(ImmutableTuple2::_2).toArray(ExpressionNode[]::new)));
    }

    public static IMatcher<ImmutableTuple2<PatternNode, ExpressionNode>> matchArm(FrameDescriptor frameDescriptor) {
        return (term, unifier) -> 
            Optional.of(M.appl2("MatchArm", PatternNode.matchPattern(frameDescriptor),
                ExpressionNode.matchExpr(frameDescriptor), (appl, p, e) -> ImmutableTuple2.of(p, e))
            .match(term, unifier)
            .orElseThrow(() -> new ParseException("Parse error on reading expression " + term)));
    }
}
