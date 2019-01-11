package mb.flowspec.primitives;

import java.util.List;
import java.util.Optional;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map.Immutable;
import mb.flowspec.controlflow.CFGNode;
import mb.flowspec.controlflow.ControlFlowGraphReader;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.terms.M;
import mb.flowspec.terms.TermIndex;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public interface IGetPropertyPrimitive {
    default Optional<? extends IStrategoTerm> getProperty(IStrategoTerm term, List<IStrategoTerm> terms,
        Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> properties) throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: key");
        }
        final Optional<ICFGNode> optNode = M.maybe(() -> ControlFlowGraphReader.cfgNode(term)).map(Optional::of)
            .orElseGet(() -> TermIndex.get(term).map(CFGNode::normal));
        return optNode.flatMap(node -> M.maybe(() -> {
            String key = M.string(terms.get(0));
            return properties.get(ImmutableTuple2.of(node, key)).get();
        }));
    }
}