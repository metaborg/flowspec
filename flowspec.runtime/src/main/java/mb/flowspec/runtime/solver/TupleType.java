package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class TupleType extends Type {
    @Parameter abstract Type left();
    @Parameter abstract Type right();
}
