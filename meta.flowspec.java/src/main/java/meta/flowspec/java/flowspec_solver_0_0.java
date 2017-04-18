package meta.flowspec.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class flowspec_solver_0_0 {
    private static final String SUCCESSOR = "successor";
    
    public static void addPropConstraint(Map<String, Map<Integer, Set<Value>>> results, IStrategoTerm term) {
        switch (term.getTermType()) {
        case IStrategoTerm.APPL:
            final IStrategoAppl appl = (IStrategoAppl) term;
            if (appl.getConstructor().getName().equals("HasProp") && appl.getSubtermCount() == 4) {
                final Optional<Integer> subject = getTermIndexNumber(appl.getSubterm(0));
                if (!subject.isPresent()) {
//                    ioAgent.printError("[WARNING] Improper input, expected TermIndex/2, got: " + appl.getSubterm(0));
                    return;
                }
                final Optional<String> propName = getString(appl.getSubterm(1));
                if (!propName.isPresent()) {
//                    ioAgent.printError("[WARNING] Improper input, expected string, got: " + appl.getSubterm(1));
                    return;
                }
                final Optional<Value> object = getValue(appl.getSubterm(2));
                if (!object.isPresent()) {
//                    ioAgent.printError("[WARNING] Improper input, expected TermIndex/2 or (TermIndex/2, string), got: "
//                            + appl.getSubterm(2));
                    return;
                }
                final Optional<List<IStrategoTerm>> conditions = getList(appl.getSubterm(3));
                if (!conditions.isPresent()) {
//                    ioAgent.printError("[WARNING] Improper input, expected List, got: " + appl.getSubterm(3));
                    return;
                }
                if (conditions.get().isEmpty()) {
                    if (!results.containsKey(propName.get())) {
                        results.put(propName.get(), new HashMap<>());
                    }
                    Map<Integer, Set<Value>> level2 = results.get(propName.get());
                    if (!level2.containsKey(subject.get())) {
                        level2.put(subject.get(), new HashSet<>());
                    }
                    Set<Value> level3 = level2.get(subject.get());
                    level3.add(object.get());
                } else {
                    
                }
            } else if (appl.getConstructor().getName().equals("CFGEdge") && appl.getSubtermCount() == 3) {
                final Optional<Integer> subject = getTermIndexNumber(appl.getSubterm(0));
                if (!subject.isPresent()) {
//                    ioAgent.printError("[WARNING] Improper input, expected TermIndex/2, got: " + appl.getSubterm(0));
                    return;
                }
                final Optional<Value> object = getTermIndexNumber(appl.getSubterm(1)).map(TermIndex::new);
                if (!object.isPresent()) {
//                    ioAgent.printError("[WARNING] Improper input, expected TermIndex/2, got: " + appl.getSubterm(0));
                    return;
                }
                final Optional<List<IStrategoTerm>> notes = getList(appl.getSubterm(2));
                if (!notes.isPresent()) {
//                    ioAgent.printError("[WARNING] Improper input, expected List, got: " + appl.getSubterm(3));
                    return;
                }
                if (!results.containsKey(SUCCESSOR)) {
                    results.put(SUCCESSOR, new HashMap<>());
                }
                Map<Integer, Set<Value>> level2 = results.get(SUCCESSOR);
                if (!level2.containsKey(subject.get())) {
                    level2.put(subject.get(), new HashSet<>());
                }
                Set<Value> level3 = level2.get(subject.get());
                level3.add(object.get());
            } else {
//                ioAgent.printError(
//                        "[WARNING] Improper input, expected HasProp/4 or CFGEdge/3, got: " + appl.getConstructor());
                return;
            }
            return;
        }
//        ioAgent.printError("[WARNING] Improper input, expected constructor of some kind, got: " + term);
    }

    private static Optional<Integer> getTermIndexNumber(IStrategoTerm term) {
        switch (term.getTermType()) {
        case IStrategoTerm.APPL:
            final IStrategoAppl appl = (IStrategoAppl) term;
            if (!appl.getConstructor().getName().equals("TermIndex") || appl.getSubtermCount() != 2) {
                return Optional.empty();
            }
            return getInt(appl.getSubterm(1));
        }
        return Optional.empty();
    }

    private static Optional<String> getString(IStrategoTerm term) {
        switch (term.getTermType()) {
        case IStrategoTerm.STRING:
            final IStrategoString string = (IStrategoString) term;
            return Optional.of(string.stringValue());
        }
        return Optional.empty();
    }

    private static Optional<Integer> getInt(IStrategoTerm term) {
        switch (term.getTermType()) {
        case IStrategoTerm.INT:
            final IStrategoInt i = (IStrategoInt) term;
            return Optional.of(i.intValue());
        }
        return Optional.empty();
    }

    private static Optional<List<IStrategoTerm>> getList(IStrategoTerm term) {
        switch (term.getTermType()) {
        case IStrategoTerm.LIST:
            final IStrategoList l = (IStrategoList) term;
            return Optional.of(StreamSupport.stream(l.spliterator(), false).collect(Collectors.toList()));
        }
        return Optional.empty();
    }

    private static Optional<Value> getValue(IStrategoTerm term) {
        Optional<Value> termindex = getTermIndexNumber(term).map(TermIndex::new);
        if (termindex.isPresent()) {
            return termindex;
        } else {
            switch (term.getTermType()) {
            case IStrategoTerm.APPL:
                final IStrategoAppl appl = (IStrategoAppl) term;
                if (appl.getConstructor().getName().equals("Var") && appl.getSubtermCount() == 2) {
                    return getInt(appl.getSubterm(0))
                            .flatMap(i1 -> getInt(appl.getSubterm(1)).map(i2 -> new Variable(i1, i2)));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * @param results
     * @param factory
     * @return The results as association lists in IStrategoTerm
     */
    public static IStrategoTerm translateResults(final Map<String, Map<Integer, Set<Value>>> results, final ITermFactory factory) {
        /* results |> map \e1 -> (key e1, value e1 |> map \e2 -> (key e2, value e2)) */
        // @formatter:off
        return makeList(
                results.entrySet().stream().map(e1 ->
                    factory.makeTuple(
                        factory.makeString(e1.getKey()),
                        translateResult(e1.getValue(), factory))),
                factory);
        // @formatter:on
    }
    
    /**
     * Helper for translateResults
     */
    private static IStrategoTerm translateResult(final Map<Integer, Set<Value>> map, final ITermFactory factory) {
        // @formatter:off
        return makeList(
                map.entrySet().stream().map(e2 ->
                    factory.makeTuple(
                        factory.makeInt(e2.getKey()),
                        makeList(e2.getValue().stream().map(v ->
                            v.toIStrategoTerm(factory)), factory))),
            factory);
        // @formatter:on
    }

    private static IStrategoList makeList(final Stream<? extends IStrategoTerm> stream, final ITermFactory factory) {
        return factory.makeList(stream.collect(Collectors.toList()));
    }
}