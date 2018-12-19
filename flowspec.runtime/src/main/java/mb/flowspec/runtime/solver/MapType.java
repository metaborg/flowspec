package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class MapType extends Type {
    @Parameter public abstract Type key();
    @Parameter public abstract Type value();
}
