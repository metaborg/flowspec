package mb.flowspec.primitives;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.core.InterpreterException;

import io.usethesource.capsule.Map.Immutable;
import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.PersistentUnifier;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public interface IGetPropertyPrimitive {
    default Optional<? extends ITerm> getProperty(ITerm term, List<ITerm> terms,
            Immutable<Tuple2<CFGNode, String>, Ref<ITerm>> properties) throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: key");
        }
        final Optional<String> key = M.stringValue().match(terms.get(0), PersistentUnifier.Immutable.of());
        final Optional<CFGNode> optNode = CFGNode.matcher().match(term).map(Optional::of)
                .orElseGet(() -> TermIndex.get(term).map(CFGNode::normal));
        return optNode.flatMap(node -> key.flatMap(k -> Optional
                .ofNullable(properties.get(ImmutableTuple2.of(node, k))))).map(r -> r.get());
    }
}