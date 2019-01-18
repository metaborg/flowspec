package mb.flowspec.runtime.interpreter.expressions;

import java.util.Map;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.ICFGNode.Kind;
import mb.flowspec.controlflow.ImmutableCFGNode;
import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.Initializable;
import mb.flowspec.terms.TermIndex;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

@NodeChildren({@NodeChild("rhs")})
public abstract class PropNode extends ExpressionNode implements Initializable {
    private Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> properties;
    protected final String propName;

    public PropNode(String propName) {
        this.propName = propName;
    }

    @Specialization
    protected IStrategoTerm lookup(ICFGNode rhs) {
        return properties.get(ImmutableTuple2.of(rhs, propName)).get();
    }

    @Specialization
    protected IStrategoTerm lookup(IStrategoTerm rhs) {
        TermIndex index = TermIndex.get(rhs).get();
        final ImmutableCFGNode node = ImmutableCFGNode.of(index, null, Kind.Normal);
        return properties.get(ImmutableTuple2.of(node, propName)).get();
    }

    @Override
    public void init(InitValues initValues) {
        this.properties = initValues.properties;
    }
}
