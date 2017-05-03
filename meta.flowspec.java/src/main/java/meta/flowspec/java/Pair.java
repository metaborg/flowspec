package meta.flowspec.java;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

@Immutable
public abstract class Pair<L,R> {
    @Parameter public abstract L left();
    @Parameter public abstract R right();
}
