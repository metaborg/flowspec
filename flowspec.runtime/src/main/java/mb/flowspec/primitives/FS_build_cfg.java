package mb.flowspec.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import mb.flowspec.controlflow.CFGNode;
import mb.flowspec.controlflow.ControlFlowGraphReader;
import mb.flowspec.controlflow.FlowSpecSolution;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.runtime.interpreter.values.EmptyMapOrSet;
import mb.flowspec.runtime.interpreter.values.Map;
import mb.flowspec.runtime.interpreter.values.Name;
import mb.flowspec.runtime.interpreter.values.Set;
import mb.flowspec.runtime.lattice.FullSetLattice;
import mb.flowspec.terms.TermIndex;
import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.stratego.StrategoBlob;

public class FS_build_cfg extends AbstractPrimitive {
    public FS_build_cfg() {
        super(FS_build_cfg.class.getSimpleName(), 0, 1);
    }

    @Override public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        initRuntimeConstructors(env.getFactory());
        if(tvars.length != 1) {
            throw new IllegalArgumentException("Expected as first term argument: analysis");
        }
        final IResult result;
        try {
            result = (IResult) ((StrategoBlob) tvars[0]).value();
        } catch(ClassCastException e) {
            throw new IllegalArgumentException("Not a valid analysis term.");
        }
        ControlFlowGraphReader builder = ControlFlowGraphReader.build(env.current());
        env.setCurrent(new StrategoBlob(
            result.withCustomAnalysis(FlowSpecSolution.of(result.solution(), builder.cfg(), builder.tfAppls()))));
        return true;
    }

    /**
     * Stratego compiler assumes constructors are maximally shared and does identity comparison.
     * So we initialize the constructors at runtime...
     */
    static void initRuntimeConstructors(ITermFactory tf) {
        CFGNode.initializeConstructor(tf);
        ICFGNode.Kind.initializeConstructor(tf);
        Set.initializeConstructor(tf);
        Map.initializeConstructor(tf);
        EmptyMapOrSet.initializeConstructor(tf);
        Name.initializeConstructor(tf);
        FullSetLattice.ISetImplementation.initializeConstructor(tf);
        TermIndex.initializeConstructor(tf);
    }
}
