package meta.flowspec.java;

import java.util.Map;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class TermIndex extends Value {
    public int index;

    public Map<Value, Value> childConditions;
    public Map<Value, Map<String, Value>> propConditions;

    /**
     * @param index
     * @param childConditions
     * @param propConditions
     */
    public TermIndex(int index, Map<Value, Value> childConditions, Map<Value, Map<String, Value>> propConditions) {
        this.index = index;
        this.childConditions = childConditions;
        this.propConditions = propConditions;
    }

    /**
     * @param index
     */
    public TermIndex(int index) {
        this.index = index;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeInt(index);
    }
}