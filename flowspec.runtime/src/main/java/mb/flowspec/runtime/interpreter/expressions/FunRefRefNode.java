package mb.flowspec.runtime.interpreter.expressions;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.values.Function;

public class FunRefRefNode extends FunRefNode {
    public final String name;
    public Function function;
    
    public FunRefRefNode(String name) {
        this.name = name;
    }

    @Override
    public void init(InitValues initValues) {
        this.function = initValues.functions().get(name);
    }
}
