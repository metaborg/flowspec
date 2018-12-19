package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.interpreter.patterns.PatternNode;

public class MatchNode extends ExpressionNode {
    @Child private ExpressionNode subject;
    @Children private PatternNode[] matchArms;
    @Children private ExpressionNode[] matchBodies;

    public MatchNode(ExpressionNode subject, PatternNode[] matchArms, ExpressionNode[] matchBodies) {
        this.subject = subject;
        this.matchArms = matchArms;
        this.matchBodies = matchBodies;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        Object value = subject.executeGeneric(frame);
        for(int i = 0; i < matchArms.length; i++) {
            PatternNode p = matchArms[i];
            if(p.matchGeneric(frame, value)) {
                return matchBodies[i].executeGeneric(frame);
            }
        }
        return null;
    }
}
