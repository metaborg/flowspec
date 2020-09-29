package mb.flowspec.runtime.interpreter.values;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;
import org.metaborg.util.task.NullCancel;
import org.metaborg.util.task.NullProgress;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderOriginTermFactory;
import org.spoofax.terms.StrategoConstructor;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.TermList;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.terms.B;
import mb.flowspec.terms.IStrategoAppl2;
import mb.flowspec.terms.ImmutableTermIndex;
import mb.nabl2.scopegraph.CriticalEdgeException;
import mb.nabl2.scopegraph.StuckException;
import mb.nabl2.scopegraph.path.IResolutionPath;
import mb.nabl2.scopegraph.terms.Label;
import mb.nabl2.scopegraph.terms.Occurrence;
import mb.nabl2.scopegraph.terms.Scope;
import mb.nabl2.scopegraph.terms.path.Paths;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.stratego.StrategoTermIndices;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.nabl2.terms.stratego.TermIndex;
import mb.nabl2.util.collections.IFunction;

@Value.Immutable
public abstract class Name implements Serializable, IStrategoAppl2 {
    private static IStrategoConstructor cons;
    private static IStrategoConstructor namespaceCons;
    private static final ITermFactory factory = new ImploderOriginTermFactory(new TermFactory());
    private static final StrategoTerms strategoTerms = new StrategoTerms(factory);

    public static void initializeConstructor(ITermFactory tf) {
        cons = tf.makeConstructor("Occurrence", 3);
        namespaceCons = tf.makeConstructor("Namespace", 1);
    }

    @Override public IStrategoConstructor getConstructor() {
        return cons != null ? cons : new StrategoConstructor(getName(), getSubtermCount());
    }

    @Value.Parameter @Value.Derived Occurrence declaration() {
        return resolutionPath().getDeclaration();
    }

    @Value.Parameter @Value.Auxiliary abstract IResolutionPath<Scope, Label, Occurrence> resolutionPath();

    public static Name fromOccurrence(InitValues initValues, Occurrence occurrence) {
        Collection<IResolutionPath<Scope, Label, Occurrence>> paths;
        try {
            paths = initValues.nameResolution.resolve(occurrence, new NullCancel(), new NullProgress());
        } catch (CriticalEdgeException | StuckException | InterruptedException e) {
            paths = Collections.emptySet();
        }
        if(paths.isEmpty()) {
            final IFunction.Immutable<Occurrence, Scope> decls = initValues.scopeGraph.getDecls();
            final Scope declScope = decls.get(occurrence)
                .orElseThrow(() -> new RuntimeException("Name " + occurrence + " cannot be resolved"));
            final IResolutionPath<Scope, Label, Occurrence> path = Paths
                .<Scope, Label, Occurrence>resolve(occurrence, Paths.decl(Paths.empty(declScope), occurrence)).get();
            return ImmutableName.of(path);
        } else if(paths.size() > 1) {
            throw new RuntimeException("Name " + occurrence + " does not resolve to a unique declaration");
        }
        return ImmutableName.of(paths.iterator().next());
    }

    @Override public String getName() {
        return declaration().getOp();
    }

    @Override public int getSubtermCount() {
        return declaration().getArity();
    }

    @Override public IStrategoTerm[] getAllSubterms() {
        final String namespace = declaration().getNamespace().getName();
        final ITerm name = declaration().getName();
        final IStrategoAppl namespaceTerm = B.appl(namespaceCons, B.string(namespace));
        IStrategoTerm nameTerm = strategoTerms.toStratego(name);
        final Optional<mb.nabl2.terms.stratego.TermIndex> optTermIndex = mb.nabl2.terms.stratego.TermIndex.get(name);
        final mb.nabl2.terms.stratego.TermIndex termIndex;
        if(optTermIndex.isPresent()) {
            termIndex = optTermIndex.get();
        } else {
            termIndex = TermIndex.matcher().match(declaration().getIndex()).get();
            nameTerm = StrategoTermIndices.put(termIndex, nameTerm, factory);
        }
        return new IStrategoTerm[] { namespaceTerm, nameTerm, ImmutableTermIndex.of(termIndex.getResource(), termIndex.getId()) };
    }

    @Override public List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(getAllSubterms());
    }
}
