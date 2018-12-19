package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;

@Immutable
public abstract class StaticInfo {
    /// Strings are names of dataflow properties
    @Parameter public abstract BinaryRelation.Immutable<String, String> dependsOn();
    /// String is dataflow property name
    @Parameter public abstract Map.Immutable<String, Metadata<?>> metadata();
    @Parameter public abstract FunctionInfo functions();
    @Parameter public abstract LatticeInfo lattices();

    public StaticInfo addAll(StaticInfo other) {
        BinaryRelation.Transient<String, String> dependsOn = this.dependsOn().asTransient();
        other.dependsOn().entryIterator().forEachRemaining(e -> {
            dependsOn.__insert(e.getKey(), e.getValue());
        });
        Map.Transient<String, Metadata<?>> metadata = this.metadata().asTransient();
        other.metadata().entryIterator().forEachRemaining(e -> {
            Metadata<?> md = metadata.get(e.getKey());
            if(md == null) {
                metadata.__put(e.getKey(), e.getValue());
            } else {
                metadata.__put(e.getKey(), md.addAll(e.getValue()));
            }
        });
        FunctionInfo functions = this.functions().addAll(other.functions());
        LatticeInfo lattices = this.lattices().addAll(other.lattices());
        return ImmutableStaticInfo.of(dependsOn.freeze(), metadata.freeze(), functions, lattices);
    }

    public static StaticInfo of() {
        return ImmutableStaticInfo.of(BinaryRelation.Immutable.of(), Map.Immutable.of(), ImmutableFunctionInfo.of(), ImmutableLatticeInfo.of());
    }
}
