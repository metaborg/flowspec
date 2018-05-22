package mb.flowspec.runtime.interpreter.values;

import java.util.Collections;
import java.util.Objects;

import com.google.common.collect.ImmutableClassToInstanceMap;

import io.usethesource.capsule.Set;
import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.scopegraph.path.IResolutionPath;
import mb.nabl2.scopegraph.path.IScopePath;
import mb.nabl2.scopegraph.terms.Label;
import mb.nabl2.scopegraph.terms.Namespace;
import mb.nabl2.scopegraph.terms.Occurrence;
import mb.nabl2.scopegraph.terms.OccurrenceIndex;
import mb.nabl2.scopegraph.terms.Scope;
import mb.nabl2.scopegraph.terms.path.Paths;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.util.collections.IFunction;
import mb.nabl2.util.collections.PSequence;

public class Name extends Occurrence implements IResolutionPath<Scope, Label, Occurrence> {
    private final IResolutionPath<Scope, Label, Occurrence> resolutionPath;

    public Name(IResolutionPath<Scope, Label, Occurrence> resolutionPath) {
        Objects.requireNonNull(resolutionPath);
        this.resolutionPath = resolutionPath;
    }

    public static Name fromOccurrence(InitValues initValues, Occurrence occurrence) {
        java.util.Set<IResolutionPath<Scope, Label, Occurrence>> paths = initValues.nameResolution().resolve(occurrence).orElse(Collections.emptySet());
        if(paths.isEmpty()) {
            final IFunction.Immutable<Occurrence, Scope> decls = initValues.scopeGraph().getDecls();
            final Scope declScope = decls.get(occurrence).orElseThrow(() -> new RuntimeException("Name " + occurrence + " cannot be resolved"));
            final IResolutionPath<Scope, Label, Occurrence> path = Paths.<Scope,Label,Occurrence>resolve(occurrence, Paths.decl(Paths.empty(declScope), occurrence)).get();
            return new Name(path);
        } else if(paths.size() > 1) {
            throw new RuntimeException("Name " + occurrence + " does not resolve to a unique declaration");
        }
        return new Name(paths.iterator().next());
    }

    @Override
    public Occurrence getReference() {
        return resolutionPath.getReference();
    }

    @Override
    public IScopePath<Scope, Label, Occurrence> getPath() {
        return resolutionPath.getPath();
    }

    @Override
    public Occurrence getDeclaration() {
        return resolutionPath.getDeclaration();
    }

    @Override
    public Set.Immutable<Occurrence> getImports() {
        return resolutionPath.getImports();
    }

    @Override
    public Set.Immutable<Scope> getScopes() {
        return resolutionPath.getScopes();
    }

    @Override
    public PSequence<Label> getLabels() {
        return resolutionPath.getLabels();
    }

    @Override
    public Iterable<IResolutionPath<Scope, Label, Occurrence>> getImportPaths() {
        return resolutionPath.getImportPaths();
    }

    @Override
    public IApplTerm withAttachments(ImmutableClassToInstanceMap<Object> value) {
        return resolutionPath.getDeclaration().withAttachments(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resolutionPath == null) ? 0 : resolutionPath.getDeclaration().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Name other = (Name) obj;
        if (resolutionPath == null) {
            if (other.resolutionPath != null)
                return false;
        } else if (!resolutionPath.getDeclaration().equals(other.resolutionPath.getDeclaration()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return resolutionPath.getDeclaration().toString();
    }

    @Override
    public Namespace getNamespace() {
        return resolutionPath.getDeclaration().getNamespace();
    }

    @Override
    public ITerm getName() {
        return resolutionPath.getDeclaration().getName();
    }

    @Override
    public OccurrenceIndex getIndex() {
        return resolutionPath.getDeclaration().getIndex();
    }
}
