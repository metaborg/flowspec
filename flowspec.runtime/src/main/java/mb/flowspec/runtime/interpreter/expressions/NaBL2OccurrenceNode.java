package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.flowspec.runtime.interpreter.values.Name;
import mb.nabl2.scopegraph.terms.ImmutableOccurrence;
import mb.nabl2.scopegraph.terms.Namespace;
import mb.nabl2.scopegraph.terms.Occurrence;
import mb.nabl2.scopegraph.terms.OccurrenceIndex;
import mb.nabl2.terms.stratego.StrategoTermIndex;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.nabl2.terms.stratego.TermIndex;

public class NaBL2OccurrenceNode extends ExpressionNode {
    private final Namespace namespace;
    private final ReadVarNode ref;
    private InitValues initValues;

    public NaBL2OccurrenceNode(Namespace ns, ReadVarNode rvn) {
        this.namespace = ns;
        this.ref = rvn;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        try {
            return executeName(frame);
        } catch(UnexpectedResultException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public Name executeName(VirtualFrame frame) throws UnexpectedResultException {
        final IStrategoTerm name = ref.executeIStrategoTerm(frame);
        StrategoTerms strategoTerms = new StrategoTerms();
        final TermIndex termIndex = strategoTerms.fromStratego(StrategoTermIndex.get(name).get());
        final Occurrence occurrence =
            ImmutableOccurrence.of(this.namespace, new StrategoTerms(null).fromStratego(name), new OccurrenceIndex(termIndex.getResource(), termIndex));
        return Name.fromOccurrence(initValues, occurrence);
    }

    public void init(InitValues initValues) {
        this.initValues = initValues;
    }
}
