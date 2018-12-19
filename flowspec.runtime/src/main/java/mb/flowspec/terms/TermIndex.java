package mb.flowspec.terms;

import java.util.Optional;

import org.immutables.value.Value;
import org.spoofax.interpreter.core.Tools;
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

    public static Optional<TermIndex> get(IStrategoTerm term) {
        for (IStrategoTerm anno : term.getAnnotations()) {
            Optional<TermIndex> index = matchTermIndex(anno);
            if (index.isPresent()) {
                return index;
            }
        }
        return Optional.empty();
    }

    public static Optional<TermIndex> matchTermIndex(IStrategoTerm term) {
        if (!(Tools.isTermAppl(term) && Tools.hasConstructor((IStrategoAppl) term, OP, ARITY))) {
            return Optional.empty();
        }
        IStrategoTerm resourceTerm = term.getSubterm(0);
        IStrategoTerm idTerm = term.getSubterm(1);
        if (!(Tools.isTermString(resourceTerm) && Tools.isTermInt(idTerm))) {
            return Optional.empty();
        }
        return Optional.of(ImmutableTermIndex.of(Tools.asJavaString(resourceTerm), Tools.asJavaInt(idTerm)));
    }
}
