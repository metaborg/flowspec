package mb.flowspec.compiled;

import javax.annotation.Nullable;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.flowspec.runtime.solver.Metadata;

/**
 * A compiled FlowSpec specification of control-flow of a language, and data-flow analyses ("properties") over that control-flow.
 */
public interface ICompiledFlowSpecProperty<T> {
    /**
     * @return The name of the data-flow property
     */
    String getName();
    /**
     * @return Either forward or backward, the direction of the analysis through the control-flow graph.
     */
    Metadata.Direction direction();

    /**
     * @return The lattice instance used by this property
     */
    CompleteLattice<T> lattice();

    /**
     * @param node the node to look up the transfer function for
     * @return the transfer function of the given node, for this property; or null if it's the identity transfer function
     */
    @Nullable ITransferFunction<T> getTransferFunction(ICFGNode node);
}
