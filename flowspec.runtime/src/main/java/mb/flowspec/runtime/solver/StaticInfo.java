package mb.flowspec.runtime.solver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.usethesource.capsule.BinaryRelation;

public class StaticInfo {
    /// Strings are names of dataflow properties
    public final BinaryRelation.Immutable<String, String> dependsOn;
    /// String is dataflow property name
    public final Map<String, Metadata<?>> metadata;
    public final FunctionInfo functions;
    public final LatticeInfo lattices;

    public StaticInfo() {
        this.dependsOn = BinaryRelation.Immutable.of();
        this.metadata = Collections.emptyMap();
        this.functions = new FunctionInfo();
        this.lattices = new LatticeInfo();
    }

    public StaticInfo(io.usethesource.capsule.BinaryRelation.Immutable<String, String> dependsOn,
        Map<String, Metadata<?>> metadata, FunctionInfo functions, LatticeInfo lattices) {
        this.dependsOn = dependsOn;
        this.metadata = metadata;
        this.functions = functions;
        this.lattices = lattices;
    }

    public StaticInfo addAll(StaticInfo other) {
        BinaryRelation.Transient<String, String> dependsOn = this.dependsOn.asTransient();
        other.dependsOn.entryIterator().forEachRemaining(e -> {
            dependsOn.__insert(e.getKey(), e.getValue());
        });
        Map<String, Metadata<?>> metadata = new HashMap<>(this.metadata);
        other.metadata.entrySet().forEach(e -> {
            Metadata<?> md = metadata.get(e.getKey());
            if(md == null) {
                metadata.put(e.getKey(), e.getValue());
            } else {
                metadata.put(e.getKey(), md.addAll(e.getValue()));
            }
        });
        FunctionInfo functions = this.functions.addAll(other.functions);
        LatticeInfo lattices = this.lattices.addAll(other.lattices);
        return new StaticInfo(dependsOn.freeze(), Collections.unmodifiableMap(metadata), functions, lattices);
    }
}
