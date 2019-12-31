package mb.flowspec.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.StrategoInt;

public class FSInt extends StrategoInt implements TermIndexed {
    protected TermIndex termIndex;

    public FSInt(int value, IStrategoList annotations) {
        super(value, annotations);
        termIndex = TermIndexed.filterAnnos(this);
    }

    @Override public TermIndex termIndex() {
        return termIndex;
    }
}
