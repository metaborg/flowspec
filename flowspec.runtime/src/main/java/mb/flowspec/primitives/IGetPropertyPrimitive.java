package mb.flowspec.primitives;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.CFGNode;
import mb.flowspec.controlflow.ControlFlowGraphReader;
import mb.flowspec.controlflow.ICFGNode;
import org.spoofax.terms.util.M;
import mb.flowspec.terms.TermIndex;

public interface IGetPropertyPrimitive {
    default Optional<? extends IStrategoTerm> getProperty(IStrategoTerm term, List<IStrategoTerm> terms,
        Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> properties) throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: key");
        }
        final Optional<ICFGNode> optNode = M.maybe(() -> ControlFlowGraphReader.cfgNode(term)).map(Optional::of)
            .orElseGet(() -> TermIndex.get(term).map(CFGNode::normal));
        return optNode.flatMap(node -> M.maybe(() -> {
            String prop = M.string(terms.get(0));
            return Optional.ofNullable(properties.get(prop)).flatMap(m -> Optional.ofNullable(m.get(node))).map(Ref::get);
        }).flatMap(v -> v));
    }
}