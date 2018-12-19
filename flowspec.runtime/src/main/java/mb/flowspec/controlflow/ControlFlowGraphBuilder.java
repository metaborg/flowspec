package mb.flowspec.controlflow;

import java.util.Arrays;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import io.usethesource.capsule.Map;
import mb.flowspec.controlflow.ICFGNode.Kind;
import mb.flowspec.terms.ImmutableTermIndex;
import mb.flowspec.terms.M;
import mb.flowspec.terms.TermIndex;
import mb.nabl2.stratego.StrategoTerms;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public class ControlFlowGraphBuilder {
    protected final ICompleteControlFlowGraph.Transient cfg;
    protected final Map.Transient<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls;

    protected ControlFlowGraphBuilder() {
        this.cfg = ImmutableTransientCompleteControlFlowGraph.of();
        this.tfAppls = Map.Transient.of();
    }

    // Public API

    public static ControlFlowGraphBuilder build(IStrategoTerm current) {
        ControlFlowGraphBuilder b = new ControlFlowGraphBuilder();
        b.process(current);
        return b;
    }

    public ICompleteControlFlowGraph.Immutable cfg() {
        return this.cfg.freeze();
    }

    public Map.Immutable<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls() {
        return this.tfAppls.freeze();
    }

    // Builder

    protected void process(IStrategoTerm current) {
        IStrategoList list = M.list(current);
        for(IStrategoTerm term : list) {
            switch(M.appl(term).getName()) {
                case "CFDirectEdge": {
                    final IStrategoAppl appl = M.appl(term, 3);
                    final ICFGNode sourceNode = cfgNode(M.at(appl, 0));
                    final ICFGNode targetNode = cfgNode(M.at(appl, 1));
                    addCFGNode(sourceNode);
                    addCFGNode(targetNode);
                    this.cfg.edges().__insert(sourceNode, targetNode);
                    break;
                }
                case "CTFAppl": {
                    final IStrategoAppl appl = M.appl(term, 5);
                    final ICFGNode cfgNode = cfgNode(M.at(appl, 0));
                    final String propName = M.string(M.at(appl, 1));
                    final String modName = M.string(M.at(appl, 2));
                    final int offset = M.integer(M.at(appl, 3));
                    final IStrategoList argsList = M.list(M.at(appl, 4));
                    final List<IStrategoTerm> args = Arrays.asList(argsList.getAllSubterms());
                    tfAppls.__put(ImmutableTuple2.of(cfgNode, propName),
                        ImmutableTransferFunctionAppl.of(modName, offset, args));
                    break;
                }
                default: {
                    throw new AssertionError("Unable to parse CFG constraint: " + term);
                }
            }
        }
    }

    public static ICFGNode cfgNode(IStrategoTerm term) {
        final IStrategoAppl appl = M.appl(term, "CFGNode", 3);
        final TermIndex index = termIndex(M.at(appl, 0));
        final String name = M.string(M.at(appl, 1));
        final ICFGNode.Kind kind = kind(M.at(appl, 2));
        return ImmutableCFGNode.of(index, name, kind);
    }

    public static TermIndex termIndex(IStrategoTerm term) {
        final IStrategoAppl appl = M.appl(term, "TermIndex", 2);
        final String resource = M.string(M.at(appl, 0));
        final int id = M.integer(M.at(appl, 1));
        return ImmutableTermIndex.of(resource, id).withAttachments(StrategoTerms.getAttachments(term));
    }

    public static Kind kind(IStrategoTerm term) {
        final IStrategoAppl appl = M.appl(term, 0);
        return ICFGNode.Kind.valueOf(appl.getName());
    }

    protected void addCFGNode(ICFGNode node) {
        switch(node.getKind()) {
            case Start:
                cfg.startNodes().__insert(node);
                break;
            case End:
                cfg.endNodes().__insert(node);
                break;
            case Entry:
                cfg.entryNodes().__insert(node);
                break;
            case Exit:
                cfg.exitNodes().__insert(node);
                break;
            case Normal:
                cfg.normalNodes().__insert(node);
                break;
        }
    }
}
