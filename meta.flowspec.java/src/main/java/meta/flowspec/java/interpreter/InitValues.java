package meta.flowspec.java.interpreter;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IBasicControlFlowGraph;
import org.metaborg.meta.nabl2.scopegraph.esop.IEsopNameResolution;
import org.metaborg.meta.nabl2.scopegraph.esop.IEsopScopeGraph;
import org.metaborg.meta.nabl2.scopegraph.terms.Label;
import org.metaborg.meta.nabl2.scopegraph.terms.Occurrence;
import org.metaborg.meta.nabl2.scopegraph.terms.Scope;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.unification.IUnifier;
import org.metaborg.meta.nabl2.util.collections.IProperties;
import org.metaborg.meta.nabl2.util.tuples.Tuple2;

import io.usethesource.capsule.Map;

@Immutable
public abstract class InitValues {
    @Parameter
    public abstract IBasicControlFlowGraph<CFGNode> controlFlowGraph();

    @Parameter
    public abstract Map<Tuple2<TermIndex, String>, ITerm> properties();

    @Parameter
    public abstract IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph();

    @Parameter
    public abstract IEsopNameResolution.Immutable<Scope, Label, Occurrence> nameResolution();

    @Parameter
    public abstract IUnifier.Immutable unifier();
    
    @Parameter
    public abstract IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties();
}
