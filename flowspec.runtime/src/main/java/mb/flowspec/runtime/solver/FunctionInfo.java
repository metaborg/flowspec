package mb.flowspec.runtime.solver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mb.flowspec.runtime.interpreter.values.Function;

public class FunctionInfo {
    public final Map<String, Function> functions;

    public FunctionInfo() {
        this.functions = Collections.emptyMap();
    }

    public FunctionInfo(Map<String, Function> functions) {
        this.functions = functions;
    }

    public FunctionInfo addAll(FunctionInfo functions) {
        Map<String, Function> m = new HashMap<>(this.functions);
        m.putAll(functions.functions);
        return new FunctionInfo(Collections.unmodifiableMap(m));
    }
}
