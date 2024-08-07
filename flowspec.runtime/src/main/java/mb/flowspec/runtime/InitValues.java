package mb.flowspec.runtime;

import java.util.Map;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.IControlFlowGraph;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.flowspec.terms.B;
import mb.nabl2.solver.SolverConfig;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.stratego.TermIndex;
import mb.nabl2.terms.unification.u.IUnifier;
import mb.nabl2.util.collections.IProperties;
import mb.scopegraph.pepm16.esop15.IEsopNameResolution;
import mb.scopegraph.pepm16.esop15.IEsopNameResolution.IResolutionCache;
import mb.scopegraph.pepm16.esop15.IEsopScopeGraph;
import mb.scopegraph.pepm16.terms.Label;
import mb.scopegraph.pepm16.terms.Occurrence;
import mb.scopegraph.pepm16.terms.Scope;

public class InitValues {
    public final IControlFlowGraph controlFlowGraph;
    public final Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> properties;
    public final IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph;
    public final IEsopNameResolution<Scope, Label, Occurrence> nameResolution;
    public final IUnifier.Immutable unifier;
    public final IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties;
    public final Map<String, Function> functions;
    @SuppressWarnings("rawtypes") public final Map<String, CompleteLattice> lattices;
    public final B termBuilder;

    public InitValues(SolverConfig config, IControlFlowGraph controlFlowGraph,
        Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> properties,
        IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph,
        IResolutionCache<Scope, Label, Occurrence> nameResolutionCache,
        mb.nabl2.terms.unification.u.IUnifier.Immutable unifier,
        mb.nabl2.util.collections.IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties,
        Map<String, Function> functions, @SuppressWarnings("rawtypes") Map<String, CompleteLattice> lattices,
        B termBuilder) {
        this.controlFlowGraph = controlFlowGraph;
        this.properties = properties;
        this.scopeGraph = scopeGraph;
        this.nameResolution = IEsopNameResolution.of(config.getResolutionParams(), scopeGraph, (s, l) -> true, nameResolutionCache);
        this.unifier = unifier;
        this.astProperties = astProperties;
        this.functions = functions;
        this.lattices = lattices;
        this.termBuilder = termBuilder;
    }
}
