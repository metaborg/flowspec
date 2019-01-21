package mb.flowspec.controlflow;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.nabl2.solver.ISolution;
import mb.nabl2.util.Tuple2;

public class FlowSpecSolution implements IFlowSpecSolution, Serializable {
    private ISolution solution;
    private IControlFlowGraph controlFlowGraph;
    private Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls;
    private Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties;
    private Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties;

    public FlowSpecSolution(ISolution solution, IControlFlowGraph controlFlowGraph,
        Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls, Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties,
        Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties) {
        this.solution = solution;
        this.controlFlowGraph = controlFlowGraph;
        this.tfAppls = tfAppls;
        this.preProperties = preProperties;
        this.postProperties = postProperties;
    }

    @Override public ISolution solution() {
        return solution;
    }

    @Override public IControlFlowGraph controlFlowGraph() {
        return controlFlowGraph;
    }

    @Override public Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls() {
        return tfAppls;
    }

    @Override public Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties() {
        return preProperties;
    }

    @Override public Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties() {
        return postProperties;
    }

    public static IFlowSpecSolution of(ISolution solution, IControlFlowGraph controlFlowGraph,
        Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls) {
        return new FlowSpecSolution(solution, controlFlowGraph, tfAppls, Collections.emptyMap(),
            Collections.emptyMap());
    }

    @Override public IFlowSpecSolution withSolution(ISolution solution) {
        return new FlowSpecSolution(solution, this.controlFlowGraph, this.tfAppls, this.preProperties, this.postProperties);
    }

    @Override public IFlowSpecSolution withProperties(Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties, Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties) {
        return new FlowSpecSolution(this.solution, this.controlFlowGraph, this.tfAppls, preProperties, postProperties);
    }
}