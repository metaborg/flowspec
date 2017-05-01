package meta.flowspec.java.stratego;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.ImmutablePair;
import meta.flowspec.java.Pair;
import meta.flowspec.java.ast.Condition;
import meta.flowspec.java.ast.ConditionalValue;
import meta.flowspec.java.ast.OptionalUtils;
import meta.flowspec.java.ast.TermIndex;
import meta.flowspec.java.ast.Value;
import meta.flowspec.java.ast.types.Type;

public class MatchSolverTerms {
    private static final String SUCCESSOR = "successor";

    public static Pair<Pair<String, TermIndex>, ConditionalValue> getPropConstraint(IStrategoTerm term)
            throws TermMatchException {
        /**
         * TODO: update to {@link Var} instead of {@link TermIndex}
         */
        Optional<IStrategoTerm[]> c1 = MatchTerm.applChildren(new StrategoConstructor("HasProp", 4), term);
        if (c1.isPresent()) {
            IStrategoTerm[] children = c1.get();
            final TermIndex subject = TermIndex.match(children[0])
                    .orElseThrow(() -> new TermMatchException("TermIndex/2", children[0].toString()));
            final String propName = MatchTerm.string(children[1])
                    .orElseThrow(() -> new TermMatchException("string", children[1].toString()));
            final Value object = Value.match(children[2]).orElseThrow(
                    () -> new TermMatchException("TermIndex/2 or (TermIndex/2, string)", children[2].toString()));
            final List<Condition> conditions = MatchTerm.list(children[3])
                    .orElseThrow(() -> new TermMatchException("list", children[3].toString())).stream()
                    .map(Condition.Utils::match).collect(OptionalUtils.toOptionalList())
                    .orElseThrow(() -> new TermMatchException("list of HasProp/3", children[3].toString()));

            return ImmutablePair.of(ImmutablePair.of(propName, subject), new ConditionalValue(object, conditions));
        } else {
            Optional<IStrategoTerm[]> c2 = MatchTerm.applChildren(new StrategoConstructor("CFGEdge", 3), term);
            if (c2.isPresent()) {
                IStrategoTerm[] children = c2.get();
                final TermIndex subject = TermIndex.match(children[0])
                        .orElseThrow(() -> new TermMatchException("TermIndex/2", children[0].toString()));
                final Value object = TermIndex.match(children[1])
                        .orElseThrow(() -> new TermMatchException("TermIndex/2", children[0].toString()));
                final List<Condition> conditions = MatchTerm.list(children[2])
                        .orElseThrow(() -> new TermMatchException("list", children[3].toString())).stream()
                        .map(Condition.Utils::match).filter(Optional::isPresent).map(Optional::get)
                        .collect(Collectors.toList());

                return ImmutablePair.of(ImmutablePair.of(SUCCESSOR, subject), new ConditionalValue(object, conditions));
            } else {
                throw new TermMatchException("HasProp/4 or CFGEdge/3", term.toString());
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static Pair<String, Type> getTypeDef(IStrategoTerm term) throws TermMatchException {
        IStrategoTuple tuple = MatchTerm.tuple(term)
                .orElseThrow(() -> new TermMatchException("tuple", term.toString()));
        if (tuple.getSubtermCount() != 2) {
            throw new TermMatchException("tuple of 2", tuple.toString());
        }
        String propname = MatchTerm.string(tuple.getSubterm(0))
                .orElseThrow(() -> new TermMatchException("string", tuple.getSubterm(0).toString()));
        Type type = Type.Utils.match(tuple.getSubterm(1))
                .orElseThrow(() -> new TermMatchException("Type", tuple.getSubterm(1).toString()));
        return ImmutablePair.of(propname, type);
    }
}