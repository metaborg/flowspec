package meta.flowspec.java.interpreter;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IdentityTFAppl;
import org.metaborg.meta.nabl2.controlflow.terms.TransferFunctionAppl;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple2;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import meta.flowspec.java.interpreter.locals.ArgToVarNode;

@TypeSystemReference(Types.class)
public class TransferFunction extends RootNode {
    private ArgToVarNode[] patternVariables;
    private Where body;

    public TransferFunction(FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        this(null, frameDescriptor, patternVariables, body);
    }

    public TransferFunction(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        super(language, frameDescriptor);
        this.patternVariables = patternVariables;
        this.body = body;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        for (ArgToVarNode pv : patternVariables) {
            pv.execute(frame);
        }
        return body.execute(frame);
    }

    public static IMatcher<TransferFunction> match(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor, ISolution solution) {
        return M.appl2("TransferFunction", ArgToVarNode.matchList(frameDescriptor), Where.match(frameDescriptor, solution), (appl, patternVariables, body) -> {
            return new TransferFunction(language, frameDescriptor, patternVariables, body);
        });
    }

    public static IMatcher<TransferFunction> match(ISolution solution) {
        return match(null, new FrameDescriptor(), solution);
    }
    
    public static IMatcher<TransferFunction[]> matchList(ISolution solution) {
        return M.listElems(
                    M.tuple2(
                        M.integerValue(), 
                        term -> TransferFunction.match(solution).match(term), 
                        (appl, i, tf) -> ImmutableTuple2.of(i,tf)))
                .map(list -> {
                    TransferFunction[] tfs = new TransferFunction[list.size()];
                    for(ImmutableTuple2<Integer, TransferFunction> t2 : list) {
                        tfs[t2._1()] = t2._2();
                    }
                    return tfs;
                });
    }

    @SuppressWarnings("unchecked")
    public static <S extends ICFGNode> Object call(TransferFunctionAppl appl, TransferFunction[] tfs, Object arg) {
        if (appl instanceof IdentityTFAppl) {
            IdentityTFAppl<S> iappl = (IdentityTFAppl<S>) appl;
            return iappl.cfg.getProperty((S) arg, iappl.prop);
        }
        appl.args[0] = arg;
        return Truffle.getRuntime().createCallTarget(tfs[appl.tfOffset]).call(appl.args);
    }
}
