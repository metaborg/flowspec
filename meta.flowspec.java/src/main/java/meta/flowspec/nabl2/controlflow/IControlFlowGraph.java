package meta.flowspec.nabl2.controlflow;

import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.util.tuples.Tuple2;

import meta.flowspec.java.interpreter.TransferFunctionAppl;
import meta.flowspec.nabl2.util.collections.IFunction;
import meta.flowspec.nabl2.util.collections.IRelation2;
import meta.flowspec.nabl2.util.collections.ISet;

public interface IControlFlowGraph<S extends ICFGNode> {

    ISet<S> getAllCFGNodes();

    ISet<S> getAllStarts();

    ISet<S> getAllEnds();


    IFunction<Tuple2<S, String>, Object> getProperties();

    IFunction<Tuple2<S, String>, TransferFunctionAppl> getTFAppls();

    IRelation2<S, S> getDirectEdges();
}