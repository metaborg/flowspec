package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;

import meta.flowspec.java.stratego.ToIStrategoTerm;

public interface Value extends ToIStrategoTerm {

    static Optional<Value> match(IStrategoTerm term) {
        return OptionalUtils.orElse(TermIndex.match(term), Variable.match(term));
    }
    
}