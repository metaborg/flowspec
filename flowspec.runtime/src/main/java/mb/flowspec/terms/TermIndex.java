package mb.flowspec.terms;

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import com.google.common.collect.ImmutableClassToInstanceMap;

import mb.nabl2.terms.Terms;
import mb.nabl2.terms.stratego.ITermIndex;
import org.spoofax.terms.TermList;
import org.spoofax.terms.util.M;

@Value.Immutable
public abstract class TermIndex implements ITermIndex, IStrategoAppl2 {
    private static final String OP = "TermIndex";
    private static final int ARITY = 2;
    private static IStrategoConstructor cons;

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

    public static void initializeConstructor(ITermFactory tf) {
        cons = tf.makeConstructor(OP, ARITY);
    }

    @Override public IStrategoConstructor getConstructor() {
        return cons != null ? cons : new StrategoConstructor(getName(), getSubtermCount());
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[] { B.string(getResource()), B.integer(getId()) };
    }

    @Override @Value.Lazy public List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(getAllSubterms());
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

    public mb.nabl2.terms.stratego.TermIndex toNaBL2TermIndex() {
        return mb.nabl2.terms.stratego.TermIndex.of(getResource(), getId());
    }
}
