package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@Immutable
public abstract class FunctionInfo {
    @Parameter
    public abstract Map.Immutable<String, Function> functions();
    
    public static IMatcher<FunctionInfo> match() {
        return M.listElems(Function.match()).map(l -> {
            Map.Transient<String, Function> map = Map.Transient.of();
            l.forEach(t -> {
                map.__put(t._1(), t._2());
            });
            return ImmutableFunctionInfo.of(map.freeze());
        });
    }
    
    public static FunctionInfo of() {
        return ImmutableFunctionInfo.of(Map.Immutable.of());
    }

    public FunctionInfo addAll(FunctionInfo functions) {
        return ImmutableFunctionInfo.of(this.functions().__putAll(functions.functions()));
    }
}
