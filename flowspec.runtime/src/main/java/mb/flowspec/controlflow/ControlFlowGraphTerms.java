package mb.flowspec.controlflow;

import java.util.Set;
import java.util.stream.Collectors;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.Sets;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map.Immutable;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public final class ControlFlowGraphTerms {

    private static final String ESCAPE_MATCH = "\\\\$0";
    private static final String RECORD_RESERVED = "[\"{}|]";
    private final ControlFlowGraph controlFlowGraph;
    private final Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> preProperties;
    private final Immutable<Tuple2<ICFGNode, String>, Ref<IStrategoTerm>> postProperties;

    private ControlFlowGraphTerms(IFlowSpecSolution solution) {
        this.controlFlowGraph = (ControlFlowGraph) solution.controlFlowGraph();
        this.preProperties = solution.preProperties();
        this.postProperties = solution.postProperties();
    }


    private String toDot() {
        final Set<String> properties = this.preProperties.keySet().stream().map(t -> t._2()).collect(Collectors.toSet());

        final Set<ICFGNode> entryExitNodes = Sets.union(controlFlowGraph.entryNodes(), controlFlowGraph.exitNodes());

        final String starts = controlFlowGraph.startNodes().stream().map(node -> nodeToDot(node, properties)).collect(Collectors.joining());
        final String ends = controlFlowGraph.endNodes().stream().map(node -> nodeToDot(node, properties)).collect(Collectors.joining());
        final String otherNodes = controlFlowGraph.normalNodes().stream().map(node -> nodeToDot(node, properties)).collect(Collectors.joining());
        final String edges = removeNodes(controlFlowGraph.edges(), entryExitNodes).tupleStream(this::edgeToDot).collect(Collectors.joining());

        return "digraph FG {\n"
             + "node [ shape = record, style = \"rounded\" ];\n"
             + starts
             + ends
             + "node [ style = \"\" ];\n"
             + otherNodes
             + edges
             + "}";
    }

    private BinaryRelation.Immutable<ICFGNode, ICFGNode> removeNodes(BinaryRelation.Immutable<ICFGNode, ICFGNode> edges, Set<ICFGNode> toRemove) {
        BinaryRelation.Transient<ICFGNode, ICFGNode> result = edges.asTransient();
        for(ICFGNode node : toRemove) {
            Set<ICFGNode> tos = result.get(node);
            Set<ICFGNode> froms = result.inverse().get(node);
            result.__remove(node); // remove node ->> tos
            for(ICFGNode from : froms) {
                result.__remove(from, node); // remove froms ->> node
                for(ICFGNode to : tos) {
                    result.__insert(from, to); // add froms -->> tos
                }
            }
        }
        return result.freeze();
    }

    private String edgeToDot(ICFGNode from, ICFGNode to) {
        return "\"" + from.toString() + "\" -> \"" + to.toString() + "\";\n";
    }

    private String nodeToDot(ICFGNode node, Set<String> properties) {
        String propNames = "{" + properties.stream().map(n -> n.replaceAll(RECORD_RESERVED, ESCAPE_MATCH)).collect(Collectors.joining("|")) + "}";
        String prePropVals = "{" + properties.stream().map(n -> prePropValToDot(node, n)).collect(Collectors.joining("|")) + "}";
        String postPropVals = "{" + properties.stream().map(n -> postPropValToDot(node, n)).collect(Collectors.joining("|")) + "}";
        final String props = "{" + propNames + "|" + prePropVals + "|" + postPropVals + "}";
        return "\"" + node.toString() + "\" [ label = \"{" + (node.getCFGNodeName() + node.getIndex().toString()).replaceAll(RECORD_RESERVED, ESCAPE_MATCH) + "|" + props + "}\" ];\n";
    }

    private String prePropValToDot(ICFGNode node, String prop) {
        IStrategoTerm prePropVal = preProperties.get(ImmutableTuple2.of(node, prop)).get();
        return prePropVal.toString().replaceAll(RECORD_RESERVED, ESCAPE_MATCH);
    }

    private String postPropValToDot(ICFGNode node, String prop) {
        IStrategoTerm postPropVal = postProperties.get(ImmutableTuple2.of(node, prop)).get();
        return postPropVal.toString().replaceAll(RECORD_RESERVED, ESCAPE_MATCH);
    }

    // static interface

    public static String toDot(IFlowSpecSolution solution) {
        return new ControlFlowGraphTerms(solution).toDot();
    }

}
