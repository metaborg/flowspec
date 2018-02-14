package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.scopegraph.terms.ImmutableOccurrence;
import org.metaborg.meta.nabl2.scopegraph.terms.Namespace;
import org.metaborg.meta.nabl2.scopegraph.terms.Occurrence;
import org.metaborg.meta.nabl2.scopegraph.terms.OccurrenceIndex;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple2;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.locals.ReadVarNode;
import meta.flowspec.java.interpreter.values.Name;

public class NaBL2OccurrenceNode extends ExpressionNode {
    private final Namespace namespace;
    private final ReadVarNode ref;
    private ISolution solution;

    public NaBL2OccurrenceNode(Namespace ns, ReadVarNode rvn) {
        this.namespace = ns;
        this.ref = rvn;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            return executeName(frame);
        } catch (UnexpectedResultException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Name executeName(VirtualFrame frame) throws UnexpectedResultException {
        final ITerm name = ref.executeITerm(frame);
        final TermIndex termIndex = TermIndex.get(name).get();
        final Occurrence occurrence = ImmutableOccurrence.of(this.namespace, name, new OccurrenceIndex(termIndex.getResource(), termIndex));
        return Name.fromOccurrence(solution, occurrence);
    }

    public static IMatcher<NaBL2OccurrenceNode> match(FrameDescriptor frameDescriptor) {
        return M.appl1(
                "NaBL2Occurrence", 
                M.appl3("Occurrence", 
                        Namespace.matcher(), 
                        M.appl1("Ref", ReadVarNode.match(frameDescriptor), (appl, rvn) -> rvn), 
                        M.appl0("FSNoIndex"), 
                        (appl, ns, rvn, a2) -> ImmutableTuple2.of(ns,  rvn)), 
                (appl, nsrvn) -> new NaBL2OccurrenceNode(nsrvn._1(), nsrvn._2()));
    }
    
    public void init(ISolution solution) {
        this.solution = solution;
    }
}
