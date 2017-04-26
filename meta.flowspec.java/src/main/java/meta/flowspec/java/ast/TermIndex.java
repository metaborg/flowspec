package meta.flowspec.java.ast;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

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
}