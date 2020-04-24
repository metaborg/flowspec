package mb.flowspec.controlflow;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import mb.flowspec.terms.B;
import mb.flowspec.terms.IStrategoAppl2;
import mb.flowspec.terms.TermIndex;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.TermList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ICFGNode extends IStrategoAppl2 {
    public static final int ARITY = 3;
    public static final String NAME = "CFGNode";

    TermIndex getIndex();

    String getCFGNodeName();

    Kind getKind();

    enum Kind implements IStrategoAppl2 {
        Normal, Start, End, Entry, Exit;

        private static IStrategoConstructor consNormal;
        private static IStrategoConstructor consStart;
        private static IStrategoConstructor consEnd;
        private static IStrategoConstructor consEntry;
        private static IStrategoConstructor consExit;

        @Override public String getName() {
            return this.name();
        }

        @Override public int getSubtermCount() {
            return 0;
        }

        @Override
        public IStrategoTerm[] getAllSubterms() {
            return TermFactory.EMPTY_TERM_ARRAY;
        }

        @Override
        public List<IStrategoTerm> getSubterms() {
            return Collections.emptyList();
        }

        @Override public boolean match(IStrategoTerm second) {
            if(this == second) {
                return true;
            }
            return second.match(this);
        }

        public static void initializeConstructor(ITermFactory tf) {
            consNormal = tf.makeConstructor(Normal.name(), 0);
            consStart = tf.makeConstructor(Start.name(), 0);
            consEnd = tf.makeConstructor(End.name(), 0);
            consEntry = tf.makeConstructor(Entry.name(), 0);
            consExit = tf.makeConstructor(Exit.name(), 0);
        }

        @Override public IStrategoConstructor getConstructor() {
            switch(this) {
                case Normal:
                    return consNormal != null ? consNormal : new StrategoConstructor(getName(), getSubtermCount());
                case Start:
                    return consStart != null ? consStart : new StrategoConstructor(getName(), getSubtermCount());
                case End:
                    return consEnd != null ? consEnd : new StrategoConstructor(getName(), getSubtermCount());
                case Entry:
                    return consEntry != null ? consEntry : new StrategoConstructor(getName(), getSubtermCount());
                case Exit:
                    return consExit != null ? consExit : new StrategoConstructor(getName(), getSubtermCount());
            }
            return new StrategoConstructor(getName(), getSubtermCount());
        }
    }

    // IStrategoAppl2

    @Override default TermIndex termIndex() {
        return getIndex();
    }

    @Override default String getName() {
        return NAME;
    }

    @Override default int getSubtermCount() {
        return ARITY;
    }

    @Override default IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[]{ getIndex(), B.string(getName()), getKind() };
    }

    @Override default List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(getIndex(), B.string(getName()), getKind());
    }

    @Override default boolean match(IStrategoTerm second) {
        if(second instanceof ICFGNode) {
            ICFGNode other = (ICFGNode) second;
            return getIndex().equals(other.getIndex()) && getName().equals(other.getName())
                && getKind().equals(other.getKind());
        }
        return second.match(this);
    }
}