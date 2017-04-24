package meta.flowspec.java.stratego;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public interface ToIStrategoTerm {
    public IStrategoTerm toIStrategoTerm(ITermFactory factory);
}