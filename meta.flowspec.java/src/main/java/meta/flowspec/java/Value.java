package meta.flowspec.java;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

abstract class Value {
    public abstract IStrategoTerm toIStrategoTerm(ITermFactory factory);
}