package meta.flowspec.java.interpreter;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
import org.metaborg.meta.nabl2.controlflow.terms.IdentityTFAppl;
import org.metaborg.meta.nabl2.controlflow.terms.TransferFunctionAppl;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;
import org.metaborg.meta.nabl2.util.tuples.ImmutableTuple2;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
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

    public static IMatcher<TransferFunction> match(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return M.appl2("TransferFunction", M.listElems(M.stringValue()), Where.match(frameDescriptor, cfg), (appl, patternVars, body) -> {
            ArgToVarNode[] patternVariables = new ArgToVarNode[patternVars.size()];
            for (int i = 0; i < patternVars.size(); i++) {
                FrameSlot slot = frameDescriptor.addFrameSlot(patternVars.get(i), FrameSlotKind.Object);
                patternVariables[i] = new ArgToVarNode(i, slot);
            }
            return new TransferFunction(language, frameDescriptor, patternVariables, body);
        });
    }

    public static IMatcher<TransferFunction> match(IControlFlowGraph<ICFGNode> cfg) {
        return match(null, new FrameDescriptor(), cfg);
    }
    
    public static IMatcher<TransferFunction[]> matchList(IControlFlowGraph<ICFGNode> cfg) {
        return M.listElems(
                    M.tuple2(
                        M.integerValue(), 
                        TransferFunction.match(cfg), 
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
        appl.args[appl.args.length-1] = arg;
        return Truffle.getRuntime().createCallTarget(tfs[appl.tfOffset]).call(appl.args);
    }
}
