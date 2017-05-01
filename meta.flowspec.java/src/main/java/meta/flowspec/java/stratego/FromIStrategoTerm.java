package meta.flowspec.java.stratego;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface FromIStrategoTerm<T> {
    public Optional<T> fromIStrategoTerm(IStrategoTerm term);
}
