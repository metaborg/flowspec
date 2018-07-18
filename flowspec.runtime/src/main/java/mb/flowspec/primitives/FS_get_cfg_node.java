package mb.flowspec.primitives;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.List;
import java.util.Optional;

import org.metaborg.util.optionals.Optionals;
import org.spoofax.interpreter.core.InterpreterException;

import mb.nabl2.controlflow.terms.ICFGNode;
import mb.nabl2.solver.ISolution;
import mb.nabl2.spoofax.primitives.AnalysisPrimitive;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.PersistentUnifier;

public class FS_get_cfg_node extends AnalysisPrimitive {

    public FS_get_cfg_node() {
        super(FS_get_cfg_node.class.getSimpleName(), 1);
    }

    @Override public Optional<? extends ITerm> call(ISolution solution, ITerm term, List<ITerm> terms)
            throws InterpreterException {
        if(terms.size() != 1) {
            throw new InterpreterException("Need one term argument: nodeKind");
        }
        final Optional<ICFGNode.Kind> nodeKind = M.appl()
                .flatMap(appl -> Optionals.ofThrowing(() -> ICFGNode.Kind.valueOf(appl.getOp())))
                .match(terms.get(0), PersistentUnifier.Immutable.of());
        return nodeKind
                .<ITerm>flatMap(kind -> TermIndex.get(term)
                        .<ITerm>flatMap(index -> 
                            solution.flowSpecSolution().controlFlowGraph().findNode(index, kind).map(node -> node)));
    }

}
