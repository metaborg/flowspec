package meta.flowspec.java;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

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
}