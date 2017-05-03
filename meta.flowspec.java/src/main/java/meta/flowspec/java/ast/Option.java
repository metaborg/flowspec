package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class Option<L extends Value> implements Value {
    public final Optional<L> option;
    
    public Option() {
        this.option = Optional.empty();
    }
    
    public Option(Optional<L> option) {
        this.option = option;
    }
    
    public Option(L value) {
        this.option = Optional.ofNullable(value);
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        if (option.isPresent()) {
            return factory.makeAppl(factory.makeConstructor("Some", 1), option.get().toIStrategoTerm(factory));
        } else {
            return factory.makeAppl(factory.makeConstructor("None", 0));
        }
    }

}
