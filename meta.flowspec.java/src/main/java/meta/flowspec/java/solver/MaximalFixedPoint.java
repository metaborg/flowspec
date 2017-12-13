package meta.flowspec.java.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.metaborg.meta.nabl2.terms.IStringTerm;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Map.Transient;
import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.java.lattice.CompleteLattice;
import meta.flowspec.java.lattice.FullSetLattice;
import meta.flowspec.java.solver.Metadata.Direction;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;
import meta.flowspec.nabl2.controlflow.impl.ControlFlowGraph;

public abstract class MaximalFixedPoint {
    @SuppressWarnings("unchecked")
    public static <CFGNode extends ICFGNode> void entryPoint(IControlFlowGraph<CFGNode> cfg, List<IStrategoTerm> tfs) {
        final Map.Transient<String, Metadata> propMetadata = Map.Transient.of();
        final BinaryRelation.Transient<String, String> propDependsOn = BinaryRelation.Transient.of();
        final Map.Transient<String, TransferFunction[]> transferFuns = Map.Transient.of();
        
        for (IStrategoTerm term : tfs) {
            readPropDataTuples(term, propMetadata, propDependsOn, transferFuns, (IControlFlowGraph<ICFGNode>) cfg);
        }
        solve(cfg, propMetadata, propDependsOn.freeze(), transferFuns);
    }

    private static void readPropDataTuples(IStrategoTerm term, Transient<String, Metadata> propMetadata,
            io.usethesource.capsule.BinaryRelation.Transient<String, String> propDependsOn,
            Transient<String, TransferFunction[]> transferFuns, IControlFlowGraph<ICFGNode> cfg) {
        if (!(term instanceof IStrategoList)) {
            throw new RuntimeException("Parse error on reading the transfer functions");
        }
        IStrategoList list = (IStrategoList) term;
        for (IStrategoTerm t : list) {
            readPropDataTuple(t, propMetadata, propDependsOn, transferFuns, cfg);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void readPropDataTuple(IStrategoTerm term, Transient<String, Metadata> propMetadata,
            io.usethesource.capsule.BinaryRelation.Transient<String, String> propDependsOn,
            Transient<String, TransferFunction[]> transferFuns, IControlFlowGraph<ICFGNode> cfg) {
        if (!(term instanceof IStrategoTuple)) {
            throw new RuntimeException("Parse error on reading the transfer functions");
        }
        IStrategoTuple tuple = (IStrategoTuple) term;
        if (tuple.getSubtermCount() != 2) {
            throw new RuntimeException("Parse error on reading the transfer functions");
        }
        String propName = Tools.javaStringAt(tuple, 0);
        IStrategoTerm propData = Tools.termAt(tuple, 1);
        if (!(propData instanceof IStrategoTuple)) {
            throw new RuntimeException("Parse error on reading the transfer functions");
        }
        IStrategoTuple propDataTuple = (IStrategoTuple) propData;
        if (propDataTuple.getSubtermCount() != 2) {
            throw new RuntimeException("Parse error on reading the transfer functions");
        }
        IStrategoTerm direction = Tools.termAt(propDataTuple, 0);
        IStrategoTerm transfers = Tools.termAt(propDataTuple, 1);

        Direction dir = Direction.fromIStrategoTerm(direction);
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

        if (!(transfers instanceof IStrategoList)) {
            throw new RuntimeException("Parse error on reading the transfer functions");
        }
        IStrategoList transfersList = (IStrategoList) transfers;
        TransferFunction[] tfs = new TransferFunction[transfersList.getSubtermCount()];
        for (IStrategoTerm transfer : transfersList) {
            if (!(transfer instanceof IStrategoTuple)) {
                throw new RuntimeException("Parse error on reading the transfer functions");
            }
            IStrategoTuple transferTuple = (IStrategoTuple) transfer;
            if (transferTuple.getSubtermCount() != 2) {
                throw new RuntimeException("Parse error on reading the transfer functions");
            }
            int index = Tools.javaIntAt(transferTuple, 0);
            IStrategoTerm transferFunction = Tools.termAt(transferTuple, 1);
            tfs[index] = TransferFunction.fromIStrategoTerm(transferFunction, cfg);
        }
        transferFuns.__put(propName, tfs);
    }

    public static <CFGNode extends ICFGNode> void solve(IControlFlowGraph<CFGNode> cfg,
            Map<String, Metadata> propMetadata, BinaryRelation.Immutable<String, String> propDependsOn,
            Map<String, TransferFunction[]> transferFuns) {
        if (propDependsOn.isEmpty() && propMetadata.size() == 1) {
            for (Entry<String, Metadata> entry : propMetadata.entrySet()) {
                solveProperty(cfg, entry.getKey(), entry.getValue(), transferFuns);
            }
        } else {
            // TODO: statically check for cycles in property dependencies in
            // FlowSpec
            List<String> propTopoOrder = topoSort(propDependsOn).get();
            Collections.reverse(propTopoOrder);

            for (String prop : propTopoOrder) {
                solveProperty(cfg, prop, propMetadata.get(prop), transferFuns);
            }
        }
    }

    private static <CFGNode extends ICFGNode> void solveProperty(IControlFlowGraph<CFGNode> cfg, String prop,
            Metadata metadata, Map<String, TransferFunction[]> transferFuns) {
        if (metadata.dir() == Metadata.Direction.FlowInsensitive) {
            solveFlowInsensitiveProperty(cfg, prop);
        } else {
            solveFlowSensitiveProperty(cfg, prop, metadata, transferFuns);
        }
    }

    private static <CFGNode extends ICFGNode> void solveFlowSensitiveProperty(IControlFlowGraph<CFGNode> icfg,
            String prop, Metadata metadata, Map<String, TransferFunction[]> transferFuns) {
        // TODO: this is an evil workaround, do better API design
        ControlFlowGraph<CFGNode> cfg = (ControlFlowGraph<CFGNode>) icfg;
        // Phase 1: initialisation
        TransferFunction[] tf = transferFuns.get(prop);

        for (CFGNode n : cfg.getAllCFGNodes()) {
            cfg.setProperty(n, prop, (meta.flowspec.java.interpreter.Set<IStringTerm>) metadata.lattice().bottom());
            // No need to set a different value for the start node, since the
            // rule for the start node will result
            // in that value, which will be propagated Phase 2.
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
        default: {
            throw new RuntimeException("Unreachable: Dataflow property direction enum has unexpected value");
        }
        }

        // TODO: start at start node (or end node in case of Backward dir)
        java.util.Set<CFGNode> workList = new java.util.HashSet<>(edges.keySet());

        while (!workList.isEmpty()) {
            final CFGNode from = workList.iterator().next();
            workList.remove(from);
            for (CFGNode to : edges.get(from)) {
                Object afterFromTF = cfg.getTFAppl(from, prop).call(tf, from);
                Object beforeToTF = cfg.getProperty(to, prop);
                // TODO: use nlte instead of !lte
                if (!metadata.lattice().lte(afterFromTF, beforeToTF)) {
                    cfg.setProperty(to, prop, (meta.flowspec.java.interpreter.Set<IStringTerm>) metadata.lattice().lub(beforeToTF, afterFromTF));
                    workList.add(to);
                }
            }
        }

        // Phase 3: Result calculation
        for (CFGNode n : cfg.getAllCFGNodes()) {
            // save pre-TF results
            cfg.setProperty(n, "pre-" + prop, (meta.flowspec.java.interpreter.Set<IStringTerm>) cfg.getProperty(n, prop));
            // put post-TF results in property name
            cfg.setProperty(n, prop, (meta.flowspec.java.interpreter.Set<IStringTerm>) cfg.getTFAppl(n, prop).call(tf, n));
        }

    }

    private static <CFGNode extends ICFGNode> void solveFlowInsensitiveProperty(IControlFlowGraph<CFGNode> cfg,
            String prop) {
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
