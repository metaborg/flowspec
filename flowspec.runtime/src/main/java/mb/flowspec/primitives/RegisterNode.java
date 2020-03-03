package mb.flowspec.primitives;

import java.util.Optional;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import mb.flowspec.controlflow.ControlFlowGraphBuilder;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.ICFGNode.Kind;
import mb.flowspec.controlflow.ImmutableCFGNode;
import mb.flowspec.terms.TermIndex;

abstract class RegisterNode extends Strategy {
    private final Kind kind;

    RegisterNode(Kind kind) {
        this.kind = kind;
    }

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current) {
        final Optional<TermIndex> optIndex = TermIndex.get(current);
        if(!optIndex.isPresent()) {
            return null;
        }
        final TermIndex index = optIndex.get();
        final String name;
        if(TermUtils.isAppl(current)) {
            name = ((IStrategoAppl) current).getName();
        } else {
            name = current.toString(0);
        }
        final ImmutableCFGNode node = ImmutableCFGNode.of(index, name, kind);
        nodes().add(node);
        return node;
    }

    public abstract Set<ICFGNode> nodes();

    public static final class RegisterStartNode extends RegisterNode {
        private final ControlFlowGraphBuilder builder;

        RegisterStartNode(ControlFlowGraphBuilder builder) {
            super(Kind.Start);
            this.builder = builder;
        }

        @Override public Set<ICFGNode> nodes() {
            return builder.startNodes();
        }
    }

    public static final class RegisterEndNode extends RegisterNode {
        private final ControlFlowGraphBuilder builder;

        RegisterEndNode(ControlFlowGraphBuilder builder) {
            super(Kind.End);
            this.builder = builder;
        }

        @Override public Set<ICFGNode> nodes() {
            return builder.endNodes();
        }
    }

    public static final class RegisterEntryNode extends RegisterNode {
        private final ControlFlowGraphBuilder builder;

        RegisterEntryNode(ControlFlowGraphBuilder builder) {
            super(Kind.Entry);
            this.builder = builder;
        }

        @Override public Set<ICFGNode> nodes() {
            return builder.entryNodes();
        }
    }

    public static final class RegisterExitNode extends RegisterNode {
        private final ControlFlowGraphBuilder builder;

        RegisterExitNode(ControlFlowGraphBuilder builder) {
            super(Kind.Exit);
            this.builder = builder;
        }

        @Override public Set<ICFGNode> nodes() {
            return builder.exitNodes();
        }
    }

    public static final class RegisterNormalNode extends Strategy {
        private static final Kind kind = Kind.Normal;
        private final ControlFlowGraphBuilder builder;

        RegisterNormalNode(ControlFlowGraphBuilder builder) {
            this.builder = builder;
        }

        @Override public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm targ1) {
            final Optional<TermIndex> optIndex = TermIndex.get(current);
            if(!optIndex.isPresent()) {
                return null;
            }
            final TermIndex index = optIndex.get();
            final String name;
            if(TermUtils.isString(targ1)) {
                name = ((IStrategoString) targ1).stringValue().replaceFirst("^_", "");
            } else {
                name = targ1.toString(0);
            }
            final ImmutableCFGNode node = ImmutableCFGNode.of(index, name, kind);
            nodes().add(node);
            return node;
        }

        public Set<ICFGNode> nodes() {
            return builder.normalNodes();
        }
    }
}