package mb.flowspec.controlflow;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.metaborg.util.Ref;
import org.metaborg.util.functions.Predicate2;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import io.usethesource.capsule.Map;
import mb.nabl2.constraints.IConstraint;
import mb.nabl2.relations.variants.IVariantRelation;
import mb.nabl2.scopegraph.esop.IEsopNameResolution;
import mb.nabl2.scopegraph.esop.IEsopScopeGraph;
import mb.nabl2.scopegraph.terms.Label;
import mb.nabl2.scopegraph.terms.Occurrence;
import mb.nabl2.scopegraph.terms.Scope;
import mb.nabl2.solver.ISolution;
import mb.nabl2.solver.SolverConfig;
import mb.nabl2.solver.messages.IMessages;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.symbolic.ISymbolicConstraints;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.IUnifier;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;
import mb.nabl2.util.collections.IProperties;

public interface IFlowSpecSolution extends ISolution, IApplTerm {
    ISolution solution();

    IControlFlowGraph controlFlowGraph();
    // TODO: change to Map.Immutable<String, Map<CFGNode, ITerm>>?
    Map.Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties();
    Map.Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties();
    /**
     * @return The transfer functions associated with each node in the control flow graph(s) by property. 
     */
    Map.Immutable<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls();

    default @Nullable TransferFunctionAppl getTFAppl(ICFGNode node, String prop) {
        return tfAppls().get(ImmutableTuple2.of(node, prop));
    }

    IFlowSpecSolution withPreProperties(Map.Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> value);
    IFlowSpecSolution withPostProperties(Map.Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> value);
    IFlowSpecSolution withSolution(ISolution solution);

    // delegate method for ISolution

    default SolverConfig config() {
        return solution().config();
    }

    default IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties() {
        return solution().astProperties();
    }

    default ISolution withAstProperties(IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties) {
        return withSolution(solution().withAstProperties(astProperties));
    }

    default IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph() {
        return solution().scopeGraph();
    }

    default ISolution withScopeGraph(IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph) {
        return withSolution(solution().withScopeGraph(scopeGraph));
    }

    default IEsopNameResolution<Scope, Label, Occurrence> nameResolution() {
        return solution().nameResolution();
    }

    default IEsopNameResolution<Scope, Label, Occurrence> nameResolution(Predicate2<Scope, Label> isEdgeComplete) {
        return solution().nameResolution(isEdgeComplete);
    }

    default Optional<IEsopNameResolution.ResolutionCache<Scope, Label, Occurrence>> nameResolutionCache() {
        return solution().nameResolutionCache();
    }

    default IProperties.Immutable<Occurrence, ITerm, ITerm> declProperties() {
        return solution().declProperties();
    }

    default ISolution withDeclProperties(IProperties.Immutable<Occurrence, ITerm, ITerm> declProperties) {
        return withSolution(solution().withDeclProperties(declProperties));
    }

    default java.util.Map<String, IVariantRelation.Immutable<ITerm>> relations() {
        return solution().relations();
    }

    default ISolution withRelations(java.util.Map<String, ? extends IVariantRelation.Immutable<ITerm>> relations) {
        return withSolution(solution().withRelations(relations));
    }

    default ISymbolicConstraints symbolic() {
        return solution().symbolic();
    }

    default ISolution withSymbolic(ISymbolicConstraints symbolic) {
        return withSolution(solution().withSymbolic(symbolic));
    }

    default IUnifier.Immutable unifier() {
        return solution().unifier();
    }

    default ISolution withUnifier(IUnifier.Immutable unifier) {
        return withSolution(solution().withUnifier(unifier));
    }

    default IMessages.Immutable messages() {
        return solution().messages();
    }

    default ISolution withMessages(IMessages.Immutable messages) {
        return withSolution(solution().withMessages(messages));
    }

    default java.util.Set<IConstraint> constraints() {
        return solution().constraints();
    }

    default ISolution withConstraints(Iterable<? extends IConstraint> constraints) {
        return withSolution(solution().withConstraints(constraints));
    }

    // Implementation of ITerm / IApplTerm

    default boolean isGround() {
        return false;
    }

    default Multiset<ITermVar> getVars() {
        return ImmutableMultiset.of();
    }

    default ImmutableClassToInstanceMap<Object> getAttachments() {
        return ImmutableClassToInstanceMap.of();
    }

    default IApplTerm withAttachments(ImmutableClassToInstanceMap<Object> value) {
        return this;
    }


    default <T> T match(Cases<T> cases) {
        return cases.caseAppl(this);
    }

    default <T, E extends Throwable> T matchOrThrow(CheckedCases<T, E> cases) throws E {
        return cases.caseAppl(this);
    }

    default String getOp() {
        return "FlowSpecSolution";
    }

    default int getArity() {
        return 0;
    }

    default List<ITerm> getArgs() {
        return Arrays.asList();
    }
}
