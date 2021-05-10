package mb.flowspec.controlflow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.metaborg.util.Ref;
import org.metaborg.util.functions.Action1;
import org.metaborg.util.tuple.Tuple2;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.Multimap;

import io.usethesource.capsule.Set;
import mb.nabl2.constraints.IConstraint;
import mb.nabl2.relations.variants.IVariantRelation;
import mb.nabl2.solver.ISolution;
import mb.nabl2.solver.SolverConfig;
import mb.nabl2.solver.messages.IMessages;
import mb.nabl2.symbolic.ISymbolicConstraints;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.IAttachments;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.build.Attachments;
import mb.nabl2.terms.stratego.TermIndex;
import mb.nabl2.terms.unification.u.IUnifier;
import mb.nabl2.util.collections.IProperties;
import mb.scopegraph.pepm16.esop15.IEsopNameResolution;
import mb.scopegraph.pepm16.esop15.IEsopScopeGraph;
import mb.scopegraph.pepm16.terms.Label;
import mb.scopegraph.pepm16.terms.Occurrence;
import mb.scopegraph.pepm16.terms.OccurrenceIndex;
import mb.scopegraph.pepm16.terms.Scope;

public interface IFlowSpecSolution extends ISolution, IApplTerm {
    ISolution solution();

    IControlFlowGraph controlFlowGraph();
    // TODO: change to Map.Immutable<String, Map<CFGNode, ITerm>>?
    Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> preProperties();
    Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> postProperties();
    /**
     * @return The transfer functions associated with each node in the control flow graph(s) by property. 
     */
    Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls();

    default @Nullable TransferFunctionAppl getTFAppl(ICFGNode node, String prop) {
        return tfAppls().get(Tuple2.of(node, prop));
    }

    IFlowSpecSolution withProperties(Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> preProperties, Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> postProperties);
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

    default @Override Multimap<OccurrenceIndex, Occurrence> astDecls() {
        return solution().astDecls();
    }

    default @Override Multimap<OccurrenceIndex, Occurrence> astRefs() {
        return solution().astRefs();
    }

    default ISolution withScopeGraph(IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph) {
        return withSolution(solution().withScopeGraph(scopeGraph));
    }

    default IEsopNameResolution<Scope, Label, Occurrence> nameResolution() {
        return solution().nameResolution();
    }

    default IEsopNameResolution.IResolutionCache<Scope, Label, Occurrence> nameResolutionCache() {
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

    default Set.Immutable<ITermVar> getVars() {
        return Set.Immutable.of();
    }

    default void visitVars(Action1<ITermVar> onVar) {
    }
    
    default IAttachments getAttachments() {
        return Attachments.empty();
    }

    default IApplTerm withAttachments(IAttachments value) {
        return this;
    }

    default boolean equals(Object other, boolean compareAttachments) {
        if (this == other) return true;
        if (!(other instanceof ITerm)) return false;
        // @formatter:off
        return equals(other)
            && (!compareAttachments || Objects.equals(this.getAttachments(), ((ITerm)other).getAttachments()));
        // @formatter:on
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
