package mb.flowspec.runtime.interpreter.values;

import java.io.Serializable;

import com.google.common.collect.ImmutableClassToInstanceMap;

import io.usethesource.capsule.Set.Immutable;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;

public class Set<K extends ITerm> implements IApplTerm, ISet<K>, Serializable {
    private final Immutable<K> set;
    private final ImmutableClassToInstanceMap<Object> attachments;

    public Set() {
        this(Immutable.of(), ImmutableClassToInstanceMap.builder().build());
    }

    public Set(Immutable<K> set) {
        this(set, ImmutableClassToInstanceMap.builder().build());
    }

    public Set(Immutable<K> set, ImmutableClassToInstanceMap<Object> attachments) {
        this.set = set;
        this.attachments = attachments;
    }

    @Override
    public Immutable<K> getSet() {
        return set;
    }

    @Override
    public ImmutableClassToInstanceMap<Object> getAttachments() {
        return this.attachments;
    }

    @Override
    public Set<K> withAttachments(ImmutableClassToInstanceMap<Object> value) {
        return new Set<>(this.getSet(), value);
    }

    @Override
    public String toString() {
        if (getSet() == null) {
            return "null";
        } else {
            return getSet().toString();
        }
    }
    @Override
    public String getOp() {
        return "Set";
    }

    /* (non-Javadoc)
     * @see mb.flowspec.runtime.interpreter.values.ISet#hashCode()
     */
    @Override
    public int hashCode() {
        return getSet().hashCode();
    }

    /* (non-Javadoc)
     * @see mb.flowspec.runtime.interpreter.values.ISet#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ISet))
            return false;
        @SuppressWarnings("rawtypes")
        ISet other = (ISet) obj;
        if (getSet() == null) {
            if (other.getSet() != null)
                return false;
        } else if (!getSet().equals(other.getSet()))
            return false;
        return getSet().equals(other.getSet());
    }
}
