package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.scopegraph.path.IResolutionPath;
import org.metaborg.meta.nabl2.scopegraph.terms.ImmutableOccurrence;
import org.metaborg.meta.nabl2.scopegraph.terms.Label;
import org.metaborg.meta.nabl2.scopegraph.terms.Namespace;
import org.metaborg.meta.nabl2.scopegraph.terms.Occurrence;
import org.metaborg.meta.nabl2.scopegraph.terms.OccurrenceIndex;
import org.metaborg.meta.nabl2.scopegraph.terms.Scope;
import org.metaborg.meta.nabl2.scopegraph.terms.path.ImmutableEmptyScopePath;
import org.metaborg.meta.nabl2.scopegraph.terms.path.ImmutableResolutionPath;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.util.collections.IFunction.Immutable;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple2;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import io.usethesource.capsule.Set;
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
        final IResolutionPath<Scope, Label, Occurrence> path = resolveOccurrence(occurrence);
        return new Name(path);
    }

    private IResolutionPath<Scope, Label, Occurrence> resolveOccurrence(final Occurrence occurrence) {
        Set.Immutable<IResolutionPath<Scope, Label, Occurrence>> paths = solution.nameResolution().resolve(occurrence).orElse(Set.Immutable.of());
        if(paths.isEmpty()) {
            final Immutable<Occurrence, Scope> decls = solution.scopeGraph().getDecls();
            final Scope declScope = decls.get(occurrence).orElseThrow(() -> new RuntimeException("Name " + occurrence + " cannot be resolved"));
            return ImmutableResolutionPath.of(occurrence, ImmutableEmptyScopePath.of(declScope), occurrence);
        } else if(paths.size() > 1) {
            throw new RuntimeException("Name " + occurrence + " does not resolve to a unique declaration");
        }
        return paths.iterator().next();
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
