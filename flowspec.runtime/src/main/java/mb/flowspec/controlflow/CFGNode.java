package mb.flowspec.controlflow;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import mb.flowspec.terms.B;
import mb.flowspec.terms.TermIndex;

@Value.Immutable
@Serial.Version(value = 42L)
public abstract class CFGNode implements ICFGNode {
    private static IStrategoConstructor cons;

    public static void initializeConstructor(ITermFactory tf) {
        cons = tf.makeConstructor(ICFGNode.NAME, ICFGNode.ARITY);
    }

    @Override public IStrategoConstructor getConstructor() {
        return cons != null ? cons : new StrategoConstructor(getName(), getSubtermCount());
    }
    // ICFGNode implementation

    @Value.Parameter public abstract TermIndex getIndex();

    // Auxiliary except when the node is artificial
    @Value.Parameter @Value.Auxiliary @Nullable @Override public abstract String getCFGNodeName();

    @Value.Parameter @Override public abstract ICFGNode.Kind getKind();

    // IStrategoTerm implementation

    @Value.Lazy @Override public IStrategoList getAnnotations() {
        return B.list(this.getIndex());
    }

    @Override public String toString() {
        return "##" + getCFGNodeName() + "." + getKind() + this.getIndex().toString();
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