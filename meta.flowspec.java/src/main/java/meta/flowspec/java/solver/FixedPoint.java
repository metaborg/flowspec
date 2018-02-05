package meta.flowspec.java.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.ControlFlowGraph;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.StrategoTerms;
import org.metaborg.meta.nabl2.terms.IStringTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple2;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple3;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.java.lattice.CompleteLattice;
import meta.flowspec.java.lattice.FullSetLattice;
import meta.flowspec.java.solver.Metadata.Direction;

public abstract class FixedPoint {
    private static final String ARTIFICIAL_PROPERTY = "__START__";

    public static void entryPoint(ISolution nabl2solution, List<IStrategoTerm> tfs) {
        final Map.Transient<String, Metadata> propMetadata = Map.Transient.of();
        final BinaryRelation.Transient<String, String> propDependsOn = BinaryRelation.Transient.of();
        final Map.Transient<String, TransferFunction[]> transferFuns = Map.Transient.of();
        final IControlFlowGraph<CFGNode> cfg = nabl2solution.controlFlowGraph();
        
        // remove artificial nodes from CFG
        cfg.complete();
        
        for (IStrategoTerm term : tfs) {
            readPropDataTuples(term, propMetadata, propDependsOn, transferFuns, nabl2solution);
        }
        solve(cfg, propMetadata, propDependsOn.freeze(), transferFuns);
    }

    private static void readPropDataTuples(IStrategoTerm term, Map.Transient<String, Metadata> propMetadata,
            BinaryRelation.Transient<String, String> propDependsOn,
            Map.Transient<String, TransferFunction[]> transferFuns, ISolution solution) {
        if (!(term instanceof IStrategoList)) {
            throw new RuntimeException("Parse error on reading the transfer functions");
        }
        IStrategoList list = (IStrategoList) term;
        for (IStrategoTerm t : list) {
            readPropDataTuple(t, propMetadata, propDependsOn, transferFuns, solution);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void readPropDataTuple(IStrategoTerm sterm, Map.Transient<String, Metadata> propMetadata,
            BinaryRelation.Transient<String, String> propDependsOn,
            Map.Transient<String, TransferFunction[]> transferFuns, ISolution solution) {
        ITerm term = StrategoTerms.fromStratego(sterm);
        
        ImmutableTuple3<String, Direction, TransferFunction[]> t3 = M.tuple2(
                M.string(), 
                M.tuple2(
                        Direction.match(), 
                        TransferFunction.matchList(solution), 
                        (appl, dir, tfs) -> ImmutableTuple2.of(dir, tfs)), 
                (appl, string, t2) -> {
                    String propName = string.getValue();
                    Direction dir = t2._1();
                    TransferFunction[] tfs = t2._2();
                    return ImmutableTuple3.of(propName,  dir, tfs);
                })
            .match(term)
            .get();
        
        String propName = t3._1();
        Direction dir = t3._2();
        TransferFunction[] tfs = t3._3();

        Type type = new Type();
        CompleteLattice lattice = (CompleteLattice) new FullSetLattice<IStringTerm>();
        switch (propName) {
            case "veryBusy":
            case "available":
                propMetadata.__put(propName, ImmutableMetadata.of(dir, lattice.flip(), type));
                break;
            default:
                propMetadata.__put(propName, ImmutableMetadata.of(dir, lattice, type));
        }

        transferFuns.__put(propName, tfs);
    }

    public static void solve(IControlFlowGraph<CFGNode> cfg,
            Map<String, Metadata> propMetadata, BinaryRelation.Immutable<String, String> propDependsOn,
            Map<String, TransferFunction[]> transferFuns) {
        { // Make sure every property is in the dependency graph at least once by adding an artificial edge.
          // This way the later topoSort of the dependency graph will give all properties and you just need
          //  to remove the artificial start node. 
            BinaryRelation.Transient<String, String> propDep = propDependsOn.asTransient();
            for (Entry<String, Metadata> entry : propMetadata.entrySet()) {
                String prop = entry.getKey();
                propDep.__insert(ARTIFICIAL_PROPERTY, prop);
            }
            propDependsOn = propDep.freeze();
        }
        // TODO: statically check for cycles in property dependencies in FlowSpec
        List<String> propTopoOrder = topoSort(propDependsOn).get();
        Collections.reverse(propTopoOrder);

        for (String prop : propTopoOrder) {
            // remove artificial start used earlier to include all properties in the dependency graph
            if(prop != ARTIFICIAL_PROPERTY) {
                solveProperty(cfg, prop, propMetadata.get(prop), transferFuns.get(prop));
            }
        }
    }

    private static void solveProperty(IControlFlowGraph<CFGNode> cfg, String prop,
            Metadata metadata, TransferFunction[] tf) {
        if (metadata.dir() == Metadata.Direction.FlowInsensitive) {
            solveFlowInsensitiveProperty(cfg, prop, tf);
        } else {
            solveFlowSensitiveProperty(cfg, prop, metadata, tf);
        }
    }

    @SuppressWarnings("unchecked")
    private static void solveFlowSensitiveProperty(IControlFlowGraph<CFGNode> icfg,
            String prop, Metadata metadata, TransferFunction[] tf) {
        // FIXME: this is an evil workaround, do better API design
        ControlFlowGraph<CFGNode> cfg = (ControlFlowGraph<CFGNode>) icfg;
        // Phase 1: initialisation

        for (CFGNode n : cfg.getAllNodes()) {
            cfg.setProperty(n, prop, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) metadata.lattice().bottom());
            // No need to set a different value for the start node, since the
            //  rule for the start node will result in that value, which will be propagated Phase 2.
        }

        // Phase 2: Fixpoint iteration
        final BinaryRelation<CFGNode, CFGNode> edges;
        switch (metadata.dir()) {
            case Forward: {
                edges = cfg.getDirectEdges();
                break;
            }
            case Backward: {
                edges = cfg.getDirectEdges().inverse();
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
        final java.util.Set<CFGNode> workList = new HashSet<>();
        workList.addAll(cfg.getAllNodes());

        while (!workList.isEmpty()) {
            final CFGNode from = workList.iterator().next();
            workList.remove(from);
            for (CFGNode to : edges.get(from)) {
                Object afterFromTF = TransferFunction.call(cfg.getTFAppl(from, prop), tf, from);
                Object beforeToTF = cfg.getProperty(to, prop);
                // TODO: use nlte instead of !lte
                if (!metadata.lattice().lte(afterFromTF, beforeToTF)) {
                    cfg.setProperty(to, prop, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) metadata.lattice().lub(beforeToTF, afterFromTF));
                    workList.add(to);
                }
            }
        }

        // Phase 3: Result calculation
        final String prePropName;
        final String postPropName;
        switch (metadata.dir()) {
            case Forward: {
                prePropName = "pre-" + prop;
                postPropName = prop;
                break;
            }
            case Backward: {
                prePropName = prop;
                postPropName = "pre-" + prop;
                break;
            }
            default: 
                throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
        for (CFGNode n : cfg.getAllNodes()) {
            // save pre-TF results
            cfg.setProperty(n, prePropName, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) cfg.getProperty(n, prop));
            // put post-TF results in property name
            cfg.setProperty(n, postPropName, (meta.flowspec.java.interpreter.values.Set<IStringTerm>) TransferFunction.call(cfg.getTFAppl(n, prop), tf, n));
        }
    }

    private static void solveFlowInsensitiveProperty(IControlFlowGraph<CFGNode> cfg,
            String prop, TransferFunction[] tf) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Interprets the relation as an edge list for a graph and gives a
     * topological sorted list of its nodes. The used algorithm is due to Kahn
     * (1962):
     * https://en.wikipedia.org/wiki/Topological_sorting#Kahn.27s_algorithm
     *
     * @param rel
     *            The relation / graph
     * @return A list of "vertices" in topological order, or an empty optional
     *         if there are cycles in the graph
     */
    public static <E> Optional<List<E>> topoSort(BinaryRelation.Immutable<E, E> rel) {
        List<E> result = new ArrayList<>();
        // The frontier is initialised with nodes that have no incoming edges.
        Set<E> frontier = new HashSet<>(rel.keySet());
        frontier.removeAll(rel.values());
        BinaryRelation.Transient<E, E> mutRel = rel.asTransient();

        // (1) Move the nodes from the frontier (no incoming edges) to the
        // result
        // (2) Remove outgoing edges of each node moved from frontier to result
        // (3) Add nodes that now no longer have incoming edges to frontier
        while (!frontier.isEmpty()) {
            E node = frontier.iterator().next();
            frontier.remove(node);
            result.add(node);
            for (E rhs : mutRel.get(node)) {
                mutRel.__remove(node, rhs);
                if (!mutRel.containsValue(rhs)) {
                    frontier.add(rhs);
                }
            }
        }
        // If graph is not empty when the frontier became empty, there must be a
        // cycle in the graph
        if (!mutRel.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result);
    }
}
