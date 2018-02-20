package meta.flowspec.java.interpreter.values;

import static org.metaborg.meta.nabl2.terms.build.TermBuild.B;

import java.util.List;

import org.metaborg.meta.nabl2.terms.IApplTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.ITermVar;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public class Set<K extends ITerm> implements IApplTerm {
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
    public <T> T match(ITerm.Cases<T> cases) {
        return cases.caseAppl(this);
    }

    @Override
    public <T, E extends Throwable> T matchOrThrow(ITerm.CheckedCases<T, E> cases)
            throws E {
        return cases.caseAppl(this);
    }

    @Override
    public String toString() {
        if (set == null) {
            return "null";
        } else {
            return set.toString();
        }
    }

    @Override
    public String getOp() {
        return "Set";
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public List<ITerm> getArgs() {
        return new ImmutableList.Builder<ITerm>().add(B.newList(this.set)).build();
    }

    @Override
    public IApplTerm withLocked(boolean locked) {
        return this;
    }
}
