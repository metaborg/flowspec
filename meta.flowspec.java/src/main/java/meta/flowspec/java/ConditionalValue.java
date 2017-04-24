package meta.flowspec.java;

import java.util.List;

public class ConditionalValue {
    public final Value value;
    public final List<Condition> conditions;

    /**
     * @param value
     * @param conditions
     */
    public ConditionalValue(Value value, List<Condition> conditions) {
        this.value = value;
        this.conditions = conditions;
    }

}
