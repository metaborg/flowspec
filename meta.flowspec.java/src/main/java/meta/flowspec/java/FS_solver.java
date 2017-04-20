package meta.flowspec.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class FS_solver extends AbstractPrimitive {

    public FS_solver() {
        super(FS_solver.class.getSimpleName(), 0, 0);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        final ITermFactory factory = env.getFactory();
//        final IOAgent ioAgent = context.getIOAgent();
        // PropName -> TermIndex -> ResultValue
        final Map<String, Map<Integer, Set<Value>>> results = new HashMap<>();
        final IStrategoTerm current = env.current();

        switch (current.getTermType()) {
        case IStrategoTerm.LIST: {
            final IStrategoList list = (IStrategoList) current;

            for (IStrategoTerm term : list) {
                flowspec_solver_0_0.addPropConstraint(results, term);
            }

            env.setCurrent(flowspec_solver_0_0.translateResults(results, factory));
            return true;
        }
        default:
            // Invalid input, fall through to return null
        }
        return false;
    }

}
