package mb.flowspec.controlflow;

import java.util.Optional;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap.Builder;

import mb.flowspec.terms.B;
import mb.flowspec.terms.TermIndex;

@Value.Immutable
@Serial.Version(value = 42L)
public abstract class CFGNode implements ICFGNode {
    // ICFGNode implementation

    @Value.Parameter public abstract TermIndex getIndex();

    // Auxiliary except when the node is artificial
    @Value.Parameter @Value.Auxiliary @Nullable @Override public abstract String getCFGNodeName();

    @Value.Parameter @Override public abstract ICFGNode.Kind getKind();

    // IApplTerm implementation

    @Value.Default public ImmutableClassToInstanceMap<Object> getAttachments() {
        return ImmutableClassToInstanceMap.of();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Value.Check protected CFGNode check() {
        if(Optional.ofNullable(this.getAttachments().getInstance(TermIndex.class)).map(ti -> ti.equals(getIndex()))
            .orElse(false)) {
            return this;
        }
        Builder<Object> newAttachments = ImmutableClassToInstanceMap.builder();
        this.getAttachments().entrySet().stream().filter(e -> e.getKey() != TermIndex.class).forEach(e -> {
            newAttachments.put((Class) e.getKey(), e.getValue());
        });
        newAttachments.put(TermIndex.class, getIndex());
        return this.withAttachments(newAttachments.build());
    }

    public abstract CFGNode withAttachments(ImmutableClassToInstanceMap<Object> value);

    @Override public abstract boolean equals(Object other);

    @Override public abstract int hashCode();

    @Override public String toString() {
        return "##" + getCFGNodeName() + this.getIndex().toString();
    }

    public static CFGNode normal(TermIndex index) {
        return normal(index, null);
    }

    public static CFGNode normal(TermIndex index, @Nullable String name) {
        return ImmutableCFGNode.of(index, name, Kind.Normal);
    }

    public static CFGNode start(TermIndex index) {
        return start(index, null);
    }

    public static CFGNode start(TermIndex index, @Nullable String name) {
        return ImmutableCFGNode.of(index, name, Kind.Start);
    }

    public static CFGNode end(TermIndex index) {
        return end(index, null);
    }

    public static CFGNode end(TermIndex index, @Nullable String name) {
        return ImmutableCFGNode.of(index, name, Kind.End);
    }

    public static CFGNode entry(TermIndex index) {
        return entry(index, null);
    }

    public static CFGNode entry(TermIndex index, @Nullable String name) {
        return ImmutableCFGNode.of(index, name, Kind.Entry);
    }

    public static CFGNode exit(TermIndex index) {
        return exit(index, null);
    }

    public static CFGNode exit(TermIndex index, @Nullable String name) {
        return ImmutableCFGNode.of(index, name, Kind.Exit);
    }

}