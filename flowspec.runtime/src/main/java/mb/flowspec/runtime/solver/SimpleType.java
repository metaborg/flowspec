package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class SimpleType extends Type {
    @Parameter abstract SimpleTypeEnum simpleType();

    public enum SimpleTypeEnum {
        Name,
        Term,
        Index,
        Int,
        String,
        Float,
        Bool
    }
}
