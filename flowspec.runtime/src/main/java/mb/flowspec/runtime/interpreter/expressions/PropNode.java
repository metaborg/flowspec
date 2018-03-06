package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.controlflow.terms.ICFGNode.Kind;
import mb.nabl2.controlflow.terms.ImmutableCFGNode;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

@NodeChildren({@NodeChild("rhs")})
public abstract class PropNode extends ExpressionNode {
    private Map<Tuple2<CFGNode, String>, ITerm> properties;
    protected final String propName;

    public PropNode(String propName) {
        this.propName = propName;
    }

    @Specialization
    protected ITerm lookup(CFGNode rhs) {
        return properties.get(ImmutableTuple2.of(rhs, propName));
    }

    @Specialization
    protected ITerm lookup(ITerm rhs) {
        TermIndex index = TermIndex.get(rhs).get();
        return properties.get(ImmutableTuple2.of(ImmutableCFGNode.of(index, null, Kind.Normal), propName));
    }

    public static IMatcher<PropNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Prop", 
                M.stringValue(),
                ReadVarNode.match(frameDescriptor), 
                (appl, propName, rhs) -> PropNodeGen.create(propName, rhs));
    }

    @Override
    public void init(InitValues initValues) {
        this.properties = initValues.properties();
    }
}
