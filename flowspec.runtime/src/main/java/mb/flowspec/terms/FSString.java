package mb.flowspec.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.StrategoString;

public class FSString extends StrategoString implements TermIndexed {
    protected TermIndex termIndex;

    public FSString(String value, IStrategoList annotations) {
        super(value, annotations);
        termIndex = TermIndexed.filterAnnos(this);
    }

    @Override public TermIndex termIndex() {
        return termIndex;
    }
}
