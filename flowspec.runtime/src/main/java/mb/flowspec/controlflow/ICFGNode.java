package mb.flowspec.controlflow;

import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.terms.B;
import mb.flowspec.terms.IStrategoAppl2;
import mb.flowspec.terms.TermIndex;

public interface ICFGNode extends IStrategoAppl2 {
    public static final String OP = "CFGNode";

    TermIndex getIndex();

    String getCFGNodeName();

    Kind getKind();

    enum Kind implements IStrategoAppl2 {
        Normal, Start, End, Entry, Exit;

        @Override public String getName() {
            return this.name();
        }

        @Override public int getSubtermCount() {
            return 0;
        }

        @Override public IStrategoTerm[] getAllSubterms() {
            return new IStrategoTerm[0];
        }

        @Override public boolean match(IStrategoTerm second) {
            if(this == second) {
                return true;
            }
            return second.match(this);
        }
    }

    // IStrategoAppl2

    @Override default TermIndex termIndex() {
        return getIndex();
    }

    @Override default String getName() {
        return OP;
    }

    @Override default int getSubtermCount() {
        return 3;
    }

    @Override default IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[] { getIndex(), B.string(getName()), getKind() };
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