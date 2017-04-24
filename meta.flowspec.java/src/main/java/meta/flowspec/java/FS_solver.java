package meta.flowspec.java;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

import meta.flowspec.java.pcollections.MapSetPRelation;
import meta.flowspec.java.pcollections.PRelation;
import meta.flowspec.java.stratego.FromIStrategoTerm;

public class FS_solver extends AbstractPrimitive {

    public FS_solver() {
        super(FS_solver.class.getSimpleName(), 0, 0);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
//        final ITermFactory factory = env.getFactory();
        final IOAgent ioAgent = ((SSLLibrary) env.getOperatorRegistry(SSLLibrary.REGISTRY_NAME)).getIOAgent();
        // PropName -> TermIndex -> ResultValue*
        final PRelation<Pair<String, TermIndex>, Value> simple = new MapSetPRelation<>();
        final PRelation<Pair<String, TermIndex>, ConditionalValue> conditional = new MapSetPRelation<>();
        final IStrategoTerm current = env.current();

        return MatchTerm.list(current).map(list -> {
            for (IStrategoTerm term : list) {
                try {
                    FromIStrategoTerm.addPropConstraint(simple, conditional, term, ioAgent);
                } catch (TermMatchException e) {
                    ioAgent.printError("[WARNING] FlowSpec solver did not receive well-formed input: " + e.getMessage());
                }
            }

//            env.setCurrent(flowspec_solver_0_0.translateResults(simple, factory));
            return true;
        }).orElse(false);
    }

}
