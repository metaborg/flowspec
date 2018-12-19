package mb.flowspec.runtime.solver;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.values.Function;

@Immutable
public abstract class FunctionInfo {
    @Parameter public abstract Map.Immutable<String, Function> functions();

    public static FunctionInfo of() {
        return ImmutableFunctionInfo.of(Map.Immutable.of());
    }

    public FunctionInfo addAll(FunctionInfo functions) {
        return ImmutableFunctionInfo.of(this.functions().__putAll(functions.functions()));
    }
}
