package meta.flowspec.nabl2.controlflow.impl;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.metaborg.util.functions.PartialFunction1;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import meta.flowspec.java.interpreter.IdentityTFAppl;
import meta.flowspec.java.interpreter.TransferFunctionAppl;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;
import meta.flowspec.nabl2.util.tuples.ImmutableTuple2;
import meta.flowspec.nabl2.util.tuples.Tuple2;

public class ControlFlowGraph<N extends ICFGNode>
    implements IControlFlowGraph<N>, Serializable {

    private final Set.Transient<N> allCFGNodes;

    private final Map.Transient<Tuple2<N, String>, TransferFunctionAppl> tfAppls;
    private final Map.Transient<Tuple2<N, String>, Object> properties;
    private final BinaryRelation.Transient<N, N> directEdges;
    private final BinaryRelation.Transient<N, N> incompleteDirectEdges;

    public ControlFlowGraph() {
        this.allCFGNodes = Set.Transient.of();

        this.tfAppls = Map.Transient.of();
        this.properties = Map.Transient.of();
        this.directEdges = BinaryRelation.Transient.of();
        this.incompleteDirectEdges = BinaryRelation.Transient.of();
    }

    @Override
    public Set<N> getAllCFGNodes() {
        return allCFGNodes;
    }

    @Override
    public Set<N> getAllStarts() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public Set<N> getAllEnds() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public Map<Tuple2<N, String>, TransferFunctionAppl> getTFAppls() {
        return tfAppls;
    }

    @Override
    public Map<Tuple2<N, String>, Object> getProperties() {
        return properties;
    }

    @Override
    public BinaryRelation<N, N> getDirectEdges() {
        return directEdges;
    }

    public void addTFAppl(N node, String prop, TransferFunctionAppl tfAppl) {
        allCFGNodes.__insert(node);
        tfAppls.__put(ImmutableTuple2.of(node, prop), tfAppl);
    }

    public void setProperty(N node, String prop, Object value) {
        allCFGNodes.__insert(node);
        properties.__put(ImmutableTuple2.of(node, prop), value);
    }

    public TransferFunctionAppl getTFAppl(N node, String prop) {
        TransferFunctionAppl transferFunctionAppl = tfAppls.get(ImmutableTuple2.of(node, prop));
        if (transferFunctionAppl == null) {
            return new IdentityTFAppl<>(this, prop);
        } else {
            return transferFunctionAppl;
        }
    }

    @Override
    public Object getProperty(N node, String prop) {
        Object transferFunctionAppl = properties.get(ImmutableTuple2.of(node, prop));
        if (transferFunctionAppl == null) {
            return new IdentityTFAppl<>(this, prop);
        } else {
            return transferFunctionAppl;
        }
    }

    public void addDirectEdge(N sourceNode, N targetNode) {
        allCFGNodes.__insert(sourceNode);
        allCFGNodes.__insert(targetNode);
        directEdges.__put(sourceNode, targetNode);
    }

    public void addIncompleteDirectEdge(N sourceNode, N targetNode) {
        incompleteDirectEdges.__put(sourceNode, targetNode);
    }

    public boolean reduce(PartialFunction1<N, N> fs) {
        return reduce(incompleteDirectEdges, fs, this::addDirectEdge);
    }

    private boolean reduce(BinaryRelation.Transient<N, N> relation, PartialFunction1<N, N> f,
            BiConsumer<N, N> add) {
        Iterable<Entry<N, N>> i = () -> relation.entryIterator();
        return StreamSupport.stream(i.spliterator(), false).flatMap(slv -> {
            return f.apply(slv.getValue()).map(x -> {
                add.accept(slv.getKey(), x);
                return Stream.of(slv);
            }).orElse(Stream.empty());
        }).map(slv -> {
            return relation.__remove(slv.getKey(), slv.getValue());
        }).findAny().isPresent();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allCFGNodes == null) ? 0 : allCFGNodes.hashCode());
        result = prime * result + ((directEdges == null) ? 0 : directEdges.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((tfAppls == null) ? 0 : tfAppls.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        ControlFlowGraph<N> other = (ControlFlowGraph<N>) obj;
        if (allCFGNodes == null) {
            if (other.allCFGNodes != null)
                return false;
        } else if (!allCFGNodes.equals(other.allCFGNodes))
            return false;
        if (directEdges == null) {
            if (other.directEdges != null)
                return false;
        } else if (!directEdges.equals(other.directEdges))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (tfAppls == null) {
            if (other.tfAppls != null)
                return false;
        } else if (!tfAppls.equals(other.tfAppls))
            return false;
        return true;
    }
    
    public static <T extends ICFGNode> ControlFlowGraph<T> of() {
    	return new ControlFlowGraph<>();
    }
}
