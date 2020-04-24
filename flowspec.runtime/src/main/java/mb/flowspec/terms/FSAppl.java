package mb.flowspec.terms;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoAppl;

public class FSAppl extends StrategoAppl implements TermIndexed {
    protected final TermIndex termIndex;

    public FSAppl(IStrategoConstructor ctor, IStrategoTerm[] kids, IStrategoList annotations) {
        super(ctor, kids, annotations);
        termIndex = TermIndexed.filterAnnos(this);
    }

    @Override public TermIndex termIndex() {
        return termIndex;
    }
}
