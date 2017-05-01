package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.stratego.MatchTerm;

public class Variable implements Value {

    public final int file_index;
    public final int analysis_index;

    /**
     * @param index
     * @param varName
     */
    public Variable(int file_index, int analysis_index) {
        this.file_index = file_index;
        this.analysis_index = analysis_index;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Var", 2), factory.makeInt(file_index),
                factory.makeInt(analysis_index));
    }

    public static Optional<Variable> match(IStrategoTerm term) {
        return MatchTerm.applChildren(new StrategoConstructor("Variable", 2), term).flatMap(children -> MatchTerm
                .integer(children[0]).flatMap(i1 -> MatchTerm.integer(children[1]).map(i2 -> new Variable(i1, i2))));
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + analysis_index;
        result = prime * result + file_index;
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
        Variable other = (Variable) obj;
        if (analysis_index != other.analysis_index)
            return false;
        if (file_index != other.file_index)
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Variable [file_index=" + file_index + ", analysis_index=" + analysis_index + "]";
    }
    
}