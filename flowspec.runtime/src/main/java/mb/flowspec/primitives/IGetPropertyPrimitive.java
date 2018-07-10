package mb.flowspec.primitives;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;

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
            Immutable<Tuple2<CFGNode, String>, ITerm> properties) throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: key");
        }
        final Optional<String> key = M.stringValue().match(terms.get(0), PersistentUnifier.Immutable.of());
        final ITerm term1 = term;
        final Immutable<Tuple2<CFGNode, String>, ITerm> properties1 = properties;
        final Optional<CFGNode> optNode = CFGNode.matcher().match(term1).map(Optional::of)
                .orElseGet(() -> TermIndex.get(term1).map(CFGNode::normal));
        return optNode.flatMap(node -> key.<ITerm>flatMap(k -> Optional
                .ofNullable(properties1.get(ImmutableTuple2.of(node, k)))));
    }
}