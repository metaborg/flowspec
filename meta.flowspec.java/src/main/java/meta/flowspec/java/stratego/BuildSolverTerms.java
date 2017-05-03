package meta.flowspec.java.stratego;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import meta.flowspec.java.ast.TermIndex;
import meta.flowspec.java.Pair;
import meta.flowspec.java.ast.Rhs;
import meta.flowspec.java.pcollections.PRelation;

public class BuildSolverTerms implements ToIStrategoTerm {
    private PRelation<Pair<String, TermIndex>, Pair<Rhs, Rhs>> results;

    public BuildSolverTerms(PRelation<Pair<String, TermIndex>, Pair<Rhs, Rhs>> results) {
        this.results = results;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return makeList(this.results.entrySet().stream().map(
                e -> factory.makeTuple(makeSTuple(e.getKey(), factory), makeTuple(e.getValue(), factory))),
                factory);
    }

    public static IStrategoList makeList(final Stream<? extends IStrategoTerm> stream, final ITermFactory factory) {
        return factory.makeList(stream.collect(Collectors.toList()));
    }

    public static <L extends ToIStrategoTerm, R extends ToIStrategoTerm> IStrategoTuple makeTuple(
            final Pair<L, R> pair, final ITermFactory factory) {
        return factory.makeTuple(pair.left().toIStrategoTerm(factory), pair.right().toIStrategoTerm(factory));
    }

    private IStrategoTerm makeSTuple(Pair<String, TermIndex> pair, ITermFactory factory) {
        return factory.makeTuple(factory.makeString(pair.left()), pair.right().toIStrategoTerm(factory));
    }
}
