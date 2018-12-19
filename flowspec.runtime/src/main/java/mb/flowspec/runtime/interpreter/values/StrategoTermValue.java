package mb.flowspec.runtime.interpreter.values;

import org.spoofax.interpreter.terms.IStrategoTerm;

public class StrategoTermValue implements IValue {
    public final IStrategoTerm wrappedValue;

    public StrategoTermValue(IStrategoTerm wrappedValue) {
        this.wrappedValue = wrappedValue;
    }

    @Override public IStrategoTerm toStrategoTerm() {
        return wrappedValue;
    }

}
