package mb.flowspec.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoTuple;

public class FSTuple extends StrategoTuple implements TermIndexed {
    protected TermIndex termIndex;

    public FSTuple(IStrategoTerm[] kids, IStrategoList annotations) {
        super(kids, annotations);
        termIndex = TermIndexed.filterAnnos(this);
    }

    @Override public TermIndex termIndex() {
        return termIndex;
    }
}
