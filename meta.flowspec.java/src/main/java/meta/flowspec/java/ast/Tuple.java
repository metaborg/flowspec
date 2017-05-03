package meta.flowspec.java.ast;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import meta.flowspec.java.Pair;
import meta.flowspec.java.stratego.BuildSolverTerms;

public class Tuple<L extends Value, R extends Value> implements Value {
    public final Pair<L, R> tuple;
    
    public Tuple(Pair<L, R> tuple) {
        this.tuple = tuple;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return BuildSolverTerms.makeTuple(tuple, factory);
    }

}
