package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class UserType extends Type {
    @Parameter
    public abstract String name();

    @Parameter
    public abstract Type[] params();
}
