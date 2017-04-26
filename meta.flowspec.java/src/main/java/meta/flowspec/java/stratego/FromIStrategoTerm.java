package meta.flowspec.java.stratego;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.pcollections.PMap;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.Condition;
import meta.flowspec.java.ConditionUtils;
import meta.flowspec.java.ConditionalValue;
import meta.flowspec.java.ImmutablePair;
import meta.flowspec.java.MatchTerm;
import meta.flowspec.java.OptionalUtils;
import meta.flowspec.java.Pair;
import meta.flowspec.java.TermIndex;
import meta.flowspec.java.TermIndexUtils;
import meta.flowspec.java.TermMatchException;
import meta.flowspec.java.Value;
import meta.flowspec.java.ValueUtils;
import meta.flowspec.java.pcollections.MapSetPRelation;
import meta.flowspec.java.pcollections.PRelation;

public class FromIStrategoTerm {
    private static final String SUCCESSOR = "successor";

    public static Pair<Pair<String, TermIndex>, ConditionalValue> getPropConstraints(IStrategoTerm term)
            throws TermMatchException {
        PRelation<Pair<String, TermIndex>, Value> simple = new MapSetPRelation<>();
        PRelation<Pair<String, TermIndex>, ConditionalValue> conditional = new MapSetPRelation<>();
        Optional<IStrategoTerm[]> c1 = MatchTerm.applChildren(new StrategoConstructor("HasProp", 4), term);
        if (c1.isPresent()) {
            IStrategoTerm[] children = c1.get();
            final TermIndex subject = TermIndexUtils.fromIStrategoTerm(children[0])
                    .orElseThrow(() -> new TermMatchException("TermIndex/2", children[0].toString()));
            final String propName = MatchTerm.string(children[1])
                    .orElseThrow(() -> new TermMatchException("string", children[1].toString()));
            final Value object = ValueUtils.fromIStrategoTerm(children[2]).orElseThrow(
                    () -> new TermMatchException("TermIndex/2 or (TermIndex/2, string)", children[2].toString()));
            final List<Condition> conditions = MatchTerm.list(children[3])
                    .orElseThrow(() -> new TermMatchException("list", children[3].toString())).stream()
                    .map(c -> ConditionUtils.fromIStrategoTerm(c)).collect(OptionalUtils.toOptionalList())
                    .orElseThrow(() -> new TermMatchException("list of HasProp/3", children[3].toString()));

            if (conditions.isEmpty()) {
                simple = simple.plus(ImmutablePair.of(propName, subject), object);
            } else {
                conditional = conditional.plus(ImmutablePair.of(propName, subject),
                        new ConditionalValue(object, conditions));
            }
        } else {
            Optional<IStrategoTerm[]> c2 = MatchTerm.applChildren(new StrategoConstructor("CFGEdge", 3), term);
            if (c2.isPresent()) {
                IStrategoTerm[] children = c2.get();
                final TermIndex subject = TermIndexUtils.fromIStrategoTerm(children[0])
                        .orElseThrow(() -> new TermMatchException("TermIndex/2", children[0].toString()));
                final Value object = TermIndexUtils.fromIStrategoTerm(children[1])
                        .orElseThrow(() -> new TermMatchException("TermIndex/2", children[0].toString()));
                final List<Condition> conditions = MatchTerm.list(children[2])
                        .orElseThrow(() -> new TermMatchException("list", children[3].toString())).stream()
                        .map(ConditionUtils::fromIStrategoTerm).filter(Optional::isPresent).map(Optional::get)
                        .collect(Collectors.toList());

                if (conditions.isEmpty()) {
                    simple = simple.plus(ImmutablePair.of(SUCCESSOR, subject), object);
                } else {
                    conditional = conditional.plus(ImmutablePair.of(SUCCESSOR, subject),
                            new ConditionalValue(object, conditions));
                }
            } else {
                throw new TermMatchException("HasProp/4 or CFGEdge/3", term.toString());
            }
        }
        return null; // TODO
//        return ImmutablePair.of(simple, conditional);
    }

//    @SuppressWarnings("rawtypes")
//    public static PMap<String, PropType> getTypeDefs(IStrategoTerm term) {
//        MatchTerm.tuple(term).map(tuple -> {
//            if(tuple.getSubtermCount() != 2) {
//                return 
//            }
//        })
//        return null;
//    }
}