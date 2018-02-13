package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.terms.generic.TB;
import org.metaborg.meta.nabl2.util.collections.IProperties;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.locals.ReadVarNode;

public class ExtPropNode extends ExpressionNode {
    private IProperties.Immutable<TermIndex, ITerm, ITerm> props;
    private final String propName;

    @Child
    private ReadVarNode rhs;

    public ExtPropNode(String propName, ReadVarNode rhs) {
        this.propName = propName;
        this.rhs = rhs;
    }

    // TODO: write a specialisation instead based on the rhs @Child field?
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return props.getValue(TermIndex.get(rhs.executeITerm(frame)).get(), TB.newString(propName));
        } catch (UnexpectedResultException e) {
            throw new TypeErrorException(e);
        }
    }

    public static IMatcher<ExtPropNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("ExtProp", 
                M.stringValue(),
                ReadVarNode.match(frameDescriptor), 
                (appl, propName, rhs) -> new ExtPropNode(propName, rhs));
    }

    @Override
    public void init(ISolution solution) {
        this.props = solution.astProperties();
    }
}
