package mb.flowspec.runtime.solver;

import java.util.Map.Entry;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;

@Immutable
public abstract class TransferFunctionInfo {
    @Parameter public abstract BinaryRelation.Immutable<String, String> dependsOn();

    @Parameter public abstract Map.Immutable<String, Metadata<?>> metadata();

    public TransferFunctionInfo addAll(TransferFunctionInfo other) {
        Map.Transient<String, Metadata<?>> propMetadata = this.metadata().asTransient();
        BinaryRelation.Transient<String, String> dependsOn = this.dependsOn().asTransient();
        for(Entry<String, Metadata<?>> e : other.metadata().entrySet()) {
            if(propMetadata.containsKey(e.getKey())) {
                propMetadata.__put(e.getKey(), propMetadata.get(e.getKey()).addAll(e.getValue()));
            } else {
                propMetadata.__put(e.getKey(), e.getValue());
            }
        }
        for(Entry<String, String> e : other.dependsOn().entrySet()) {
            dependsOn.__insert(e.getKey(), e.getValue());
        }
        return ImmutableTransferFunctionInfo.of(dependsOn.freeze(), propMetadata.freeze());
    }

    public static TransferFunctionInfo of() {
        return ImmutableTransferFunctionInfo.of(BinaryRelation.Immutable.of(), Map.Immutable.of());
    }
}
