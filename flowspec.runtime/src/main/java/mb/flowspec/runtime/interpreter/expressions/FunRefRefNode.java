package mb.flowspec.runtime.interpreter.expressions;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.Initializable;
import mb.flowspec.runtime.interpreter.values.Function;

public class FunRefRefNode implements Initializable, FunRefNode {
    public final String name;
    public Function function;

    public FunRefRefNode(String name) {
        this.name = name;
    }

    @Override public void init(InitValues initValues) {
        this.function = initValues.functions().get(name);
    }
}
