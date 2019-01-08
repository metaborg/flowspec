package mb.flowspec.primitives;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.ICFGNode.Kind;
import mb.flowspec.controlflow.ICompleteControlFlowGraph.Transient;
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
        if(current instanceof IStrategoAppl) {
            name = ((IStrategoAppl) current).getName();
        } else {
            name = current.toString(0);
        }
        final ImmutableCFGNode node = ImmutableCFGNode.of(index, name, kind);
        nodes().__insert(node);
        return node;
    }

    public abstract io.usethesource.capsule.Set.Transient<ICFGNode> nodes();

    public static final class RegisterStartNode extends RegisterNode {
        private final Transient cfg;

        RegisterStartNode(Transient cfg) {
            super(Kind.Start);
            this.cfg = cfg;
        }

        @Override public io.usethesource.capsule.Set.Transient<ICFGNode> nodes() {
            return cfg.startNodes();
        }
    }

    public static final class RegisterEndNode extends RegisterNode {
        private final Transient cfg;

        RegisterEndNode(Transient cfg) {
            super(Kind.End);
            this.cfg = cfg;
        }

        @Override public io.usethesource.capsule.Set.Transient<ICFGNode> nodes() {
            return cfg.endNodes();
        }
    }

    public static final class RegisterEntryNode extends RegisterNode {
        private final Transient cfg;

        RegisterEntryNode(Transient cfg) {
            super(Kind.Entry);
            this.cfg = cfg;
        }

        @Override public io.usethesource.capsule.Set.Transient<ICFGNode> nodes() {
            return cfg.entryNodes();
        }
    }

    public static final class RegisterExitNode extends RegisterNode {
        private final Transient cfg;

        RegisterExitNode(Transient cfg) {
            super(Kind.Exit);
            this.cfg = cfg;
        }

        @Override public io.usethesource.capsule.Set.Transient<ICFGNode> nodes() {
            return cfg.exitNodes();
        }
    }

    public static final class RegisterNormalNode extends Strategy {
        private static final Kind kind = Kind.Normal;
        private final Transient cfg;

        RegisterNormalNode(Transient cfg) {
            this.cfg = cfg;
        }

        @Override public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm targ1) {
            final Optional<TermIndex> optIndex = TermIndex.get(current);
            if(!optIndex.isPresent()) {
                return null;
            }
            final TermIndex index = optIndex.get();
            final String name;
            if(targ1 instanceof IStrategoString) {
                name = ((IStrategoString) targ1).stringValue().replaceFirst("^_", "");
            } else {
                name = targ1.toString(0);
            }
            final ImmutableCFGNode node = ImmutableCFGNode.of(index, name, kind);
            nodes().__insert(node);
            return node;
        }

        public io.usethesource.capsule.Set.Transient<ICFGNode> nodes() {
            return cfg.normalNodes();
        }
    }
}