package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple2;
import org.metaborg.meta.nabl2.util.tuples.Tuple2;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import io.usethesource.capsule.Map;
import meta.flowspec.java.interpreter.InitValues;
import meta.flowspec.java.interpreter.locals.ReadVarNode;

@NodeChildren({@NodeChild("rhs")})
public abstract class PropNode extends ExpressionNode {
    private Map<Tuple2<TermIndex, String>, ITerm> properties;
    protected final String propName;

    public PropNode(String propName) {
        this.propName = propName;
    }

    @Specialization
    protected ITerm lookup(ITerm rhs) {
        return properties.get(ImmutableTuple2.of(TermIndex.get(rhs).get(), propName));
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
