package mb.flowspec.controlflow;

import java.util.Set;

public interface IControlFlowGraph {
    /**
     * @return The next nodes of a node in (one of) the control flow graph(s).
     */
    Set<ICFGNode> nextNodes(ICFGNode from);

    /**
     * @return The previous nodes of a node in (one of) the control flow graph(s).
     */
    Set<ICFGNode> prevNodes(ICFGNode from);

    /**
     * @return The start nodes of the control flow graph(s).
     */
    Set<ICFGNode> startNodes();

    /**
     * @return The end nodes of the control flow graph(s).
     */
    Set<ICFGNode> endNodes();

    /**
     * @return The entry nodes of the control flow graph(s).
     */
    Set<ICFGNode> entryNodes();

    /**
     * @return The exit nodes of the control flow graph(s).
     */
    Set<ICFGNode> exitNodes();

    /**
     * @return All nodes that are not start or end nodes
     */
    Set<ICFGNode> normalNodes();

    /**
     * @return The starting basic blocks of the control flow graph(s).
     */
    Set<IBasicBlock> startBlocks();

    /**
     * @return The ending basic blocks of the control flow graph(s).
     */
    Set<IBasicBlock> endBlocks();

    /**
     * @return The next basic blocks of the control flow graph(s).
     */
    Set<IBasicBlock> nextBlocks(IBasicBlock block);

    /**
     * @return The previous basic blocks of the control flow graph(s).
     */
    Set<IBasicBlock> prevBlocks(IBasicBlock block);

    /**
     * @return The topologically sorted sets of basic blocks that form strongly connected components, with reverse postorder in the SCCs
     */
    Iterable<Set<IBasicBlock>> topoSCCs();

    /**
     * @return The reverse topologically sorted sets of basic blocks that form strongly connected components, with reverse postorder in the SCCs
     */
    Iterable<Set<IBasicBlock>> revTopoSCCs();

    /**
     * @return Total number of nodes in the graph
     */
    int nodeCount();

    /**
     * @return Total number of edges in the graph
     */
    int edgeCount();

    /**
     * @return Total number of blocks in the graph
     */
    int blockCount();

    /**
     * @return Total number of edges between blocks in the graph
     */
    int blockEdgeCount();
}
