package mb.flowspec.runtime.interpreter.values;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IValue {
    public IStrategoTerm toStrategoTerm();
}
