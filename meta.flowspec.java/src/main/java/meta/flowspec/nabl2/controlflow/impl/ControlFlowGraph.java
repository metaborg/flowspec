package meta.flowspec.nabl2.controlflow.impl;

import java.io.Serializable;

import meta.flowspec.java.interpreter.IdentityTFAppl;
import meta.flowspec.java.interpreter.TransferFunctionAppl;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;
import meta.flowspec.nabl2.util.collections.HashFunction;
import meta.flowspec.nabl2.util.collections.HashRelation2;
import meta.flowspec.nabl2.util.collections.HashSet;
import meta.flowspec.nabl2.util.collections.IFunction;
import meta.flowspec.nabl2.util.collections.IRelation2;
import meta.flowspec.nabl2.util.collections.ISet;
import meta.flowspec.nabl2.util.tuples.ImmutableTuple2;
import meta.flowspec.nabl2.util.tuples.Tuple2;

public class ControlFlowGraph<S extends ICFGNode>
    implements IControlFlowGraph<S>, Serializable {

    private final ISet.Mutable<S> allCFGNodes;

    private final IFunction.Mutable<Tuple2<S, String>, TransferFunctionAppl> tfAppls;
    private final IFunction.Mutable<Tuple2<S, String>, Object> properties;
    private final IRelation2.Mutable<S, S> directEdges;

    public ControlFlowGraph() {
        this.allCFGNodes = HashSet.create();

        this.tfAppls = HashFunction.create();
        this.properties = HashFunction.create();
        this.directEdges = HashRelation2.create();
    }

    @Override
    public ISet<S> getAllCFGNodes() {
        return allCFGNodes;
    }

    @Override
    public ISet<S> getAllStarts() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public ISet<S> getAllEnds() {
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
