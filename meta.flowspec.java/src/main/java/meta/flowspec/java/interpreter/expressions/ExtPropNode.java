package meta.flowspec.java.interpreter.expressions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.metaborg.meta.nabl2.scopegraph.terms.Occurrence;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.IListTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.terms.generic.TB;

import com.google.common.collect.ImmutableList;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.locals.ReadVarNode;
import meta.flowspec.java.interpreter.values.Name;

public class ExtPropNode extends ExpressionNode {
    private ISolution solution;
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
            Optional<ITerm> nabl2value = solution.astProperties().getValue(TermIndex.get(rhs.executeITerm(frame)).get(), TB.newAppl("Property", TB.newString(propName))).map(solution.unifier()::find);
            List<Occurrence> value = nabl2value.flatMap(term -> M.listElems(Occurrence.matcher(), (t, list) -> list).match(term)).orElseGet(() -> ImmutableList.<Occurrence>builder().build());
            IListTerm list = TB.newList(value.stream().map(occ -> Name.fromOccurrence(solution, occ)).collect(Collectors.toList()));
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
    public void init(ISolution solution) {
        this.solution = solution;
    }
}
