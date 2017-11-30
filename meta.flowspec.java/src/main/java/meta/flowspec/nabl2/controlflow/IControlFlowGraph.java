package meta.flowspec.nabl2.controlflow;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.SetMultimap;
import meta.flowspec.java.interpreter.TransferFunctionAppl;
import meta.flowspec.nabl2.util.tuples.Tuple2;

public interface IControlFlowGraph<N extends ICFGNode> {

    Set<N> getAllCFGNodes();

    Set<N> getAllStarts();

    Set<N> getAllEnds();


    Map<Tuple2<N, String>, Object> getProperties();

    Map<Tuple2<N, String>, TransferFunctionAppl> getTFAppls();

    BinaryRelation<N, N> getDirectEdges();

    Object getProperty(N node, String prop);

    boolean isEmpty();
}