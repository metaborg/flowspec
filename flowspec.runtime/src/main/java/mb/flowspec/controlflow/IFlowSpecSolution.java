package mb.flowspec.controlflow;

import java.util.Arrays;
import java.util.Objects;

import jakarta.annotation.Nullable;

import org.metaborg.util.Ref;
import org.metaborg.util.collection.CapsuleUtil;
import org.metaborg.util.collection.ImList;
import org.metaborg.util.functions.Action1;
import org.metaborg.util.tuple.Tuple2;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.SetMultimap;
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
    java.util.Map<String, java.util.Map<ICFGNode, Ref<IStrategoTerm>>> preProperties();
    java.util.Map<String, java.util.Map<ICFGNode, Ref<IStrategoTerm>>> postProperties();
    /**
     * @return The transfer functions associated with each node in the control flow graph(s) by property. 
     */
    java.util.Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls();

    default @Nullable TransferFunctionAppl getTFAppl(ICFGNode node, String prop) {
        return tfAppls().get(Tuple2.of(node, prop));
    }

    IFlowSpecSolution withProperties(java.util.Map<String, java.util.Map<ICFGNode, Ref<IStrategoTerm>>> preProperties, java.util.Map<String, java.util.Map<ICFGNode, Ref<IStrategoTerm>>> postProperties);
    IFlowSpecSolution withSolution(ISolution solution);

    // delegate method for ISolution

    default @Override SolverConfig config() {
        return solution().config();
    }

    default @Override IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties() {
        return solution().astProperties();
    }

    default @Override ISolution withAstProperties(IProperties.Immutable<TermIndex, ITerm, ITerm> astProperties) {
        return withSolution(solution().withAstProperties(astProperties));
    }

    default @Override IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph() {
        return solution().scopeGraph();
    }

    default @Override SetMultimap.Immutable<OccurrenceIndex, Occurrence> astDecls() {
        return solution().astDecls();
    }

    default @Override SetMultimap.Immutable<OccurrenceIndex, Occurrence> astRefs() {
        return solution().astRefs();
    }

    default @Override ISolution withScopeGraph(IEsopScopeGraph.Immutable<Scope, Label, Occurrence, ITerm> scopeGraph) {
        return withSolution(solution().withScopeGraph(scopeGraph));
    }

    default @Override IEsopNameResolution<Scope, Label, Occurrence> nameResolution() {
        return solution().nameResolution();
    }

    default @Override IEsopNameResolution.IResolutionCache<Scope, Label, Occurrence> nameResolutionCache() {
        return solution().nameResolutionCache();
    }

    default @Override IProperties.Immutable<Occurrence, ITerm, ITerm> declProperties() {
        return solution().declProperties();
    }

    default @Override ISolution withDeclProperties(IProperties.Immutable<Occurrence, ITerm, ITerm> declProperties) {
        return withSolution(solution().withDeclProperties(declProperties));
    }

    default @Override Map.Immutable<String, IVariantRelation.Immutable<ITerm>> relations() {
        return solution().relations();
    }

    default @Override ISolution withRelations(Map.Immutable<String, IVariantRelation.Immutable<ITerm>> relations) {
        return withSolution(solution().withRelations(relations));
    }

    default @Override ISymbolicConstraints symbolic() {
        return solution().symbolic();
    }

    default @Override ISolution withSymbolic(ISymbolicConstraints symbolic) {
        return withSolution(solution().withSymbolic(symbolic));
    }

    default @Override IUnifier.Immutable unifier() {
        return solution().unifier();
    }

    default @Override ISolution withUnifier(IUnifier.Immutable unifier) {
        return withSolution(solution().withUnifier(unifier));
    }

    default @Override IMessages.Immutable messages() {
        return solution().messages();
    }

    default @Override ISolution withMessages(IMessages.Immutable messages) {
        return withSolution(solution().withMessages(messages));
    }

    default @Override Set.Immutable<IConstraint> constraints() {
        return solution().constraints();
    }

    default @Override ISolution withConstraints(Set.Immutable<IConstraint> constraints) {
        return withSolution(solution().withConstraints(constraints));
    }

    // Implementation of ITerm / IApplTerm

    default @Override boolean isGround() {
        return false;
    }

    default @Override Set.Immutable<ITermVar> getVars() {
        return CapsuleUtil.immutableSet();
    }

    default @Override void visitVars(Action1<ITermVar> onVar) {
    }

    default @Override IAttachments getAttachments() {
        return Attachments.empty();
    }

    default @Override IApplTerm withAttachments(IAttachments value) {
        return this;
    }

    default @Override boolean equals(Object other, boolean compareAttachments) {
        if (this == other) return true;
        if (!(other instanceof ITerm)) return false;
        // @formatter:off
        return equals(other)
            && (!compareAttachments || Objects.equals(this.getAttachments(), ((ITerm)other).getAttachments()));
        // @formatter:on
    }

    default @Override <T> T match(Cases<T> cases) {
        return cases.caseAppl(this);
    }

    default @Override <T, E extends Throwable> T matchOrThrow(CheckedCases<T, E> cases) throws E {
        return cases.caseAppl(this);
    }

    default @Override String getOp() {
        return "FlowSpecSolution";
    }

    default @Override int getArity() {
        return 0;
    }

    default @Override ImList.Immutable<ITerm> getArgs() {
        return ImList.Immutable.of();
    }
}
