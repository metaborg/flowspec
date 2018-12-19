package mb.flowspec.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.controlflow.ControlFlowGraphBuilder;
import mb.flowspec.controlflow.FlowSpecSolution;
import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.stratego.StrategoBlob;

public class FS_build_cfg extends AbstractPrimitive {
    public FS_build_cfg() {
        super(FS_build_cfg.class.getSimpleName(), 0, 1);
    }

    @Override public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        if(tvars.length != 1) {
            throw new IllegalArgumentException("Expected as first term argument: analysis");
        }
        final IResult result;
        try {
            result = (IResult) ((StrategoBlob) tvars[0]).value();
        } catch(ClassCastException e) {
            throw new IllegalArgumentException("Not a valid analysis term.");
        }
        ControlFlowGraphBuilder builder = ControlFlowGraphBuilder.build(env.current());
        env.setCurrent(new StrategoBlob(
            result.withCustomAnalysis(FlowSpecSolution.of(result.solution(), builder.cfg(), builder.tfAppls()))));
        return true;
    }
}
