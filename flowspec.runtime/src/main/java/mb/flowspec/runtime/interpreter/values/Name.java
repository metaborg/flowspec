package mb.flowspec.runtime.interpreter.values;

import java.io.Serializable;
import java.util.Collections;

import org.immutables.value.Value;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.terms.IStrategoAppl2;
import mb.nabl2.scopegraph.path.IResolutionPath;
import mb.nabl2.scopegraph.terms.Label;
import mb.nabl2.scopegraph.terms.Occurrence;
import mb.nabl2.scopegraph.terms.Scope;
import mb.nabl2.scopegraph.terms.path.Paths;
import mb.nabl2.util.collections.IFunction;

@Value.Immutable
public abstract class Name implements Serializable, IStrategoAppl2 {
    @Value.Parameter @Value.Derived Occurrence declaration() {
        return resolutionPath().getDeclaration();
    }

    @Value.Parameter @Value.Auxiliary abstract IResolutionPath<Scope, Label, Occurrence> resolutionPath();

    public static Name fromOccurrence(InitValues initValues, Occurrence occurrence) {
        java.util.Set<IResolutionPath<Scope, Label, Occurrence>> paths =
            initValues.nameResolution.resolve(occurrence).orElse(Collections.emptySet());
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
        return new IStrategoTerm[] {};
    }
}
