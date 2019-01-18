package mb.flowspec.runtime;

import java.util.Optional;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.metaborg.util.Ref;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.IControlFlowGraph;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.flowspec.terms.B;
import mb.nabl2.scopegraph.esop.IEsopNameResolution;
import mb.nabl2.scopegraph.esop.IEsopScopeGraph;
import mb.nabl2.scopegraph.esop.lazy.EsopNameResolution;
import mb.nabl2.scopegraph.terms.Label;
import mb.nabl2.scopegraph.terms.Occurrence;
import mb.nabl2.scopegraph.terms.Scope;
import mb.nabl2.solver.SolverConfig;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.IUnifier;
import mb.nabl2.util.Tuple2;
import mb.nabl2.util.collections.IProperties;

@Immutable
public abstract class InitValues {

    @Parameter public abstract SolverConfig config();

    @Parameter public abstract IControlFlowGraph controlFlowGraph();

    @Parameter public abstract Map<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> properties();

    @Parameter public abstract IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph();

    public IEsopNameResolution<Scope, Label, Occurrence> nameResolution() {
        final EsopNameResolution<Scope, Label, Occurrence> nr =
            EsopNameResolution.of(config().getResolutionParams(), scopeGraph(), (s, l) -> true);
        nameResolutionCache().ifPresent(nr::addAll);
        return nr;
    }

    @Auxiliary public abstract Optional<IEsopNameResolution.ResolutionCache<Scope, Label, Occurrence>>
        nameResolutionCache();


    @Parameter public abstract IUnifier.Immutable unifier();

    @Parameter public abstract IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties();

    @Parameter public abstract Map<String, Function> functions();

    @SuppressWarnings("rawtypes") @Parameter public abstract Map<String, CompleteLattice> lattices();

    @Parameter public abstract B termBuilder();
}
