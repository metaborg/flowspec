package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.stratego.MatchTerm;

public class TermIndex implements Value, Dependency {
    public final String file;
    public final int index;

    /**
     * @param index
     */
    public TermIndex(String file, int index) {
        this.file = file;
        this.index = index;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("TermIndex", 2), factory.makeString(file),
                factory.makeInt(index));
    }

    public static Optional<TermIndex> match(IStrategoTerm term) {
        return MatchTerm.applChildren(new StrategoConstructor("TermIndex", 2), term).flatMap(children -> MatchTerm
                .string(children[0]).flatMap(s -> MatchTerm.integer(children[1]).map(i -> new TermIndex(s, i))));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + index;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermIndex other = (TermIndex) obj;
        if (file == null) {
            if (other.file != null)
                return false;
        } else if (!file.equals(other.file))
            return false;
        if (index != other.index)
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TermIndex [file=" + file + ", index=" + index + "]";
    }
    
    
}