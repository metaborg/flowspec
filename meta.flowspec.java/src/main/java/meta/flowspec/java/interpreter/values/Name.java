package meta.flowspec.java.interpreter.values;

import java.util.Objects;

import org.metaborg.meta.nabl2.scopegraph.path.IResolutionPath;
import org.metaborg.meta.nabl2.scopegraph.path.IScopePath;
import org.metaborg.meta.nabl2.scopegraph.terms.Label;
import org.metaborg.meta.nabl2.scopegraph.terms.Namespace;
import org.metaborg.meta.nabl2.scopegraph.terms.Occurrence;
import org.metaborg.meta.nabl2.scopegraph.terms.OccurrenceIndex;
import org.metaborg.meta.nabl2.scopegraph.terms.Scope;
import org.metaborg.meta.nabl2.scopegraph.terms.path.ImmutableEmptyScopePath;
import org.metaborg.meta.nabl2.scopegraph.terms.path.ImmutableResolutionPath;
import org.metaborg.meta.nabl2.terms.IApplTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.util.collections.IFunction;
import org.metaborg.meta.nabl2.util.collections.PSequence;

import com.google.common.collect.ImmutableClassToInstanceMap;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.Set.Immutable;
import meta.flowspec.java.interpreter.InitValues;

public class Name extends Occurrence implements IResolutionPath<Scope, Label, Occurrence> {
    private final IResolutionPath<Scope, Label, Occurrence> resolutionPath;

    public Name(IResolutionPath<Scope, Label, Occurrence> resolutionPath) {
        Objects.requireNonNull(resolutionPath);
        this.resolutionPath = resolutionPath;
    }

    public static Name fromOccurrence(InitValues initValues, Occurrence occurrence) {
        Set.Immutable<IResolutionPath<Scope, Label, Occurrence>> paths = initValues.nameResolution().resolve(occurrence).orElse(Set.Immutable.of());
        if(paths.isEmpty()) {
            final IFunction.Immutable<Occurrence, Scope> decls = initValues.scopeGraph().getDecls();
            final Scope declScope = decls.get(occurrence).orElseThrow(() -> new RuntimeException("Name " + occurrence + " cannot be resolved"));
            return new Name(ImmutableResolutionPath.of(occurrence, ImmutableEmptyScopePath.of(declScope), occurrence));
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
    public Immutable<Occurrence> getImports() {
        return resolutionPath.getImports();
    }

    @Override
    public Immutable<Scope> getScopes() {
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
    public IApplTerm withLocked(boolean locked) {
        return resolutionPath.getDeclaration().withLocked(locked);
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
