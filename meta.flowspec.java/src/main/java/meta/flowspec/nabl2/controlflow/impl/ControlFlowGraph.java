package meta.flowspec.nabl2.controlflow.impl;

import java.io.Serializable;

import meta.flowspec.java.interpreter.IdentityTFAppl;
import meta.flowspec.java.interpreter.TransferFunctionAppl;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;
import meta.flowspec.nabl2.util.collections.HashTrieFunction;
import meta.flowspec.nabl2.util.collections.HashTrieRelation2;
import meta.flowspec.nabl2.util.collections.IFunction;
import meta.flowspec.nabl2.util.collections.IRelation2;
import meta.flowspec.nabl2.util.tuples.ImmutableTuple2;
import meta.flowspec.nabl2.util.tuples.Tuple2;
import io.usethesource.capsule.Set;

public class ControlFlowGraph<S extends ICFGNode>
    implements IControlFlowGraph<S>, Serializable {

    private final Set.Transient<S> allCFGNodes;

    private final IFunction.Transient<Tuple2<S, String>, TransferFunctionAppl> tfAppls;
    private final IFunction.Transient<Tuple2<S, String>, Object> properties;
    private final IRelation2.Transient<S, S> directEdges;

    public ControlFlowGraph() {
        this.allCFGNodes = Set.Transient.of();

        this.tfAppls = HashTrieFunction.Transient.of();
        this.properties = HashTrieFunction.Transient.of();
        this.directEdges = HashTrieRelation2.Transient.of();
    }

    @Override
    public Set<S> getAllCFGNodes() {
        return allCFGNodes;
    }

    @Override
    public Set<S> getAllStarts() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public Set<S> getAllEnds() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public IFunction<Tuple2<S, String>, TransferFunctionAppl> getTFAppls() {
        return tfAppls;
    }

    @Override
    public IFunction<Tuple2<S, String>, Object> getProperties() {
        return properties;
    }

    @Override
    public IRelation2<S, S> getDirectEdges() {
        return directEdges;
    }

    public void addTFAppl(S node, String prop, TransferFunctionAppl tfAppl) {
        allCFGNodes.add(node);
        tfAppls.put(ImmutableTuple2.of(node, prop), tfAppl);
    }

    public void setProperty(S node, String prop, Object value) {
        allCFGNodes.add(node);
        properties.put(ImmutableTuple2.of(node, prop), value);
    }

    public TransferFunctionAppl getTFAppl(S node, String prop) {
        return tfAppls.get(ImmutableTuple2.of(node, prop)).orElse(new IdentityTFAppl<>(this, prop));
    }

    @Override
    public Object getProperty(S node, String prop) {
        return properties.get(ImmutableTuple2.of(node, prop)).orElse(null);
    }

    public void addDirectEdge(S sourceNode, S targetNode) {
        allCFGNodes.add(sourceNode);
        allCFGNodes.add(targetNode);
        directEdges.put(sourceNode, targetNode);
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
        ControlFlowGraph<S> other = (ControlFlowGraph<S>) obj;
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
}
