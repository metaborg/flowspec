package mb.flowspec.compiled;

import java.util.List;

import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.IControlFlowGraph;

/**
 * A compiled FlowSpec specification of control-flow of a language, and data-flow analyses ("properties") over that control-flow.
 */
public interface ICompiledFlowSpec {
    /**
     * Passes the name resolution service which can be used for labelled jumps in building the control flow graph, and
     * in any {@see ICompiledFlowSpecProperty} for normalizing names to their definition.
     */
    void init(IResolutionService resolutionService, IAstPropertyService astPropertyService);

    /**
     * @param ast The abstract syntax tree of the program to be analyzed
     * @return The control-flow graph (technically can contain multiple graphs) of the program
     * @throws MalformedASTException When the given AST has a form that does not fit the control-flow graph rules
     */
    IControlFlowGraph buildCFG(IStrategoTerm ast) throws MalformedASTException;

    /**
     * @return A list of flowspec properties (containing the property rules), in topological order.
     */
    List<ICompiledFlowSpecProperty<?>> topoProperties();
}
