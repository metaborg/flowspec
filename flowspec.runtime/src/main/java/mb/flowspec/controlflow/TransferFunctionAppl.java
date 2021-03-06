package mb.flowspec.controlflow;

import java.io.Serializable;
import java.util.List;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;
import org.spoofax.interpreter.terms.IStrategoTerm;

@Value.Immutable
public abstract class TransferFunctionAppl implements Serializable {
    @Parameter public abstract String moduleName();
    @Parameter public abstract int offset();
    @Parameter protected abstract List<IStrategoTerm> otherArgs();

    public IStrategoTerm[] args(IStrategoTerm firstArg) {
        List<IStrategoTerm> otherArgs = otherArgs();
        IStrategoTerm[] args = new IStrategoTerm[otherArgs.size()+1];
        args[0] = firstArg;
        int i = 1;
        for(IStrategoTerm arg : otherArgs) {
            args[i] = arg;
            i++;
        }
        return args;
    }

    public Object[] args() {
        return otherArgs().toArray();
    }

    @Override
    public String toString() {
        return "(" + moduleName() + ", " + offset() + ", " + otherArgs().toString() + ")";
    }
}
