package meta.flowspec.java.ast;

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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConditionalValue other = (ConditionalValue) obj;
        if (conditions == null) {
            if (other.conditions != null)
                return false;
        } else if (!conditions.equals(other.conditions))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConditionalValue [value=" + value + ", conditions=" + conditions + "]";
    }

}
