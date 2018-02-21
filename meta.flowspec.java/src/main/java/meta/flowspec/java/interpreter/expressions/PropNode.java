package meta.flowspec.java.interpreter.expressions;

import static org.metaborg.meta.nabl2.terms.matching.TermMatch.M;

import org.metaborg.meta.nabl2.controlflow.terms.ImmutableCFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode.Kind;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.matching.TermMatch.IMatcher;
import org.metaborg.meta.nabl2.util.ImmutableTuple2;
import org.metaborg.meta.nabl2.util.Tuple2;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;

import io.usethesource.capsule.Map;
import meta.flowspec.java.interpreter.InitValues;
import meta.flowspec.java.interpreter.locals.ReadVarNode;

@NodeChildren({@NodeChild("rhs")})
public abstract class PropNode extends ExpressionNode {
    private Map<Tuple2<CFGNode, String>, ITerm> properties;
    protected final String propName;

    public PropNode(String propName) {
        this.propName = propName;
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
