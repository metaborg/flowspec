package mb.flowspec.terms;

import java.util.Optional;

import org.immutables.value.Value;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.ImmutableClassToInstanceMap;

import mb.nabl2.stratego.ITermIndex;
import mb.nabl2.terms.Terms;

@Value.Immutable
public abstract class TermIndex implements ITermIndex, IStrategoAppl2 {
    private static final String OP = "TermIndex";
    private static final int ARITY = 2;

    @Override @Value.Parameter public abstract String getResource();

    @Override @Value.Parameter public abstract int getId();

    @Value.Auxiliary @Value.Default public ImmutableClassToInstanceMap<Object> getAttachments() {
        return Terms.NO_ATTACHMENTS;
    }

    @Override public String getName() {
        return OP;
    }

    @Override public int getSubtermCount() {
        return ARITY;
    }

    @Override @Value.Lazy public IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[] { B.string(getResource()), B.integer(getId()) };
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(getResource());
        sb.append(":");
        sb.append(getId());
        return sb.toString();
    }

    @Override public TermIndex termIndex() {
        return this;
    }

    public static Optional<TermIndex> get(IStrategoTerm term) {
        if(term instanceof TermIndexed) {
            return Optional.ofNullable(((TermIndexed) term).termIndex());
        }
        for(IStrategoTerm anno : term.getAnnotations()) {
            Optional<TermIndex> index = matchTermIndex(anno);
            if(index.isPresent()) {
                return index;
            }
        }
        return Optional.empty();
    }

    public static Optional<TermIndex> matchTermIndex(IStrategoTerm term) {
        return M.maybe(() -> {
            IStrategoAppl appl = M.appl(term, OP, ARITY);
            String resource = M.string(M.at(appl, 0));
            int id = M.integer(M.at(appl, 1));
            return ImmutableTermIndex.of(resource, id);
        });
    }
}
