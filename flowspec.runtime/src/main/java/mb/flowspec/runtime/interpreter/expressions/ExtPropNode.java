package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.flowspec.runtime.interpreter.values.Name;
import mb.nabl2.scopegraph.terms.Occurrence;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.IListTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.terms.unification.PersistentUnifier;

public class ExtPropNode extends ExpressionNode {
    private InitValues initValues;
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
            Optional<ITerm> nabl2value = initValues.astProperties().getValue(TermIndex.get(rhs.executeITerm(frame)).get(), B.newAppl("Property", B.newString(propName))).map(initValues.unifier()::findRecursive);
            List<Occurrence> value = nabl2value.flatMap(term -> M.listElems(Occurrence.matcher(), (t, list) -> list).match(term, PersistentUnifier.Immutable.of())).orElseGet(() -> ImmutableList.<Occurrence>builder().build());
            IListTerm list = B.newList(value.stream().map(occ -> Name.fromOccurrence(initValues, occ)).collect(Collectors.toList()));
            return list;
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
    public void init(InitValues initValues) {
        this.initValues = initValues;
    }
}
