package mb.flowspec.primitives;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.CallT;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.InteropCallT;
import org.strategoxt.lang.InteropContext;

import mb.flowspec.controlflow.ControlFlowGraphBuilder;
import mb.flowspec.controlflow.FlowSpecSolution;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.TransferFunctionAppl;
import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.stratego.StrategoBlob;
import mb.nabl2.util.Tuple2;

public class FS_create_cfg extends AbstractPrimitive {
    public FS_create_cfg() {
        super(FS_create_cfg.class.getSimpleName(), 1, 1);
    }

    @Override public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        FS_build_cfg.initRuntimeConstructors(env.getFactory());
        if(svars.length != 1 || !(svars[0] instanceof CallT)) {
            throw new IllegalArgumentException("Expected as first strategy argument: cfg register strategy with 7 sargs (nstart, nend, nentry, nexit, nnormal, edge, appl)");
        }
        if(tvars.length != 1) {
            throw new IllegalArgumentException("Expected as first term argument: analysis");
        }
        final IResult result;
        try {
            result = (IResult) ((StrategoBlob) tvars[0]).value();
        } catch(ClassCastException e) {
            throw new IllegalArgumentException("Not a valid analysis term.");
        }
        final ControlFlowGraphBuilder builder = ControlFlowGraphBuilder.of();
        final Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls = new HashMap<>();

        final InteropContext context = (InteropContext) env;

        final Strategy[] strategyArgs = new Strategy[] {
            //nstart
            new InteropCallT(new RegisterNode.RegisterStartNode(builder), context.getContext()),
            //nend
            new InteropCallT(new RegisterNode.RegisterEndNode(builder), context.getContext()),
            //nentry
            new InteropCallT(new RegisterNode.RegisterEntryNode(builder), context.getContext()),
            //nexit
            new InteropCallT(new RegisterNode.RegisterExitNode(builder), context.getContext()),
            //nnormal
            new InteropCallT(new RegisterNode.RegisterNormalNode(builder), context.getContext()),
            //edge
            new InteropCallT(new RegisterEdge(builder), context.getContext()),
            //appl
            new InteropCallT(new RegisterAppl(tfAppls), context.getContext()),
        };
        if(!((CallT) svars[0]).evaluateWithArgs(env, strategyArgs, new IStrategoTerm[0])) {
            return false;
        }
        env.setCurrent(new StrategoBlob(
            result.withCustomAnalysis(FlowSpecSolution.of(result.solution(), builder.build(), Collections.unmodifiableMap(tfAppls)))));
        return true;
    }
}
