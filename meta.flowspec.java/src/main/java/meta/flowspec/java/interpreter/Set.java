package meta.flowspec.java.interpreter;

import java.util.stream.Collectors;

import org.metaborg.meta.nabl2.terms.IConsTerm;
import org.metaborg.meta.nabl2.terms.IListTerm;
import org.metaborg.meta.nabl2.terms.INilTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.ITermVar;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public class Set<K extends ITerm> implements IListTerm, INilTerm, IConsTerm {
    public final io.usethesource.capsule.Set.Immutable<K> set;
    private final ImmutableClassToInstanceMap<Object> attachments;

    public Set() {
        this(io.usethesource.capsule.Set.Immutable.of(), ImmutableClassToInstanceMap.builder().build());
    }

    public Set(io.usethesource.capsule.Set.Immutable<K> set) {
        this(set, ImmutableClassToInstanceMap.builder().build());
    }

    public Set(io.usethesource.capsule.Set.Immutable<K> set, ImmutableClassToInstanceMap<Object> attachments) {
        this.set = set;
        this.attachments = attachments;
    }

    @Override
    public boolean isGround() {
        return true;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public IListTerm withLocked(boolean locked) {
        return this;
    }

    @Override
    public Multiset<ITermVar> getVars() {
        return ImmutableMultiset.of();
    }

    @Override
    public ImmutableClassToInstanceMap<Object> getAttachments() {
        return this.attachments;
    }

    @Override
    public Set<K> withAttachments(ImmutableClassToInstanceMap<Object> value) {
        return new Set<>(this.set, value);
    }

    @Override
    public <T> T match(IListTerm.Cases<T> cases) {
        if (this.set.isEmpty()) {
            return cases.caseNil(this);
        } else {
            return cases.caseCons(this);
        }
    }

    @Override
    public <T, E extends Throwable> T matchOrThrow(IListTerm.CheckedCases<T, E> cases) throws E {
        if (this.set.isEmpty()) {
            return cases.caseNil(this);
        } else {
            return cases.caseCons(this);
        }
    }

    @Override
    public <T> T match(ITerm.Cases<T> cases) {
        return cases.caseList(this);
    }

    @Override
    public <T, E extends Throwable> T matchOrThrow(ITerm.CheckedCases<T, E> cases)
            throws E {
        return cases.caseList(this);
    }

    @Override
    public K getHead() {
        return (K) this.set.iterator().next();
    }

    @Override
    public Set<K> getTail() {
        return new Set<>(this.set.__remove(this.getHead()), this.attachments);
    }
    
    @Override
    public String toString() {
        return set.stream().collect(Collectors.toSet()).toString();
    }
}
