package mb.flowspec.runtime.interpreter;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import io.usethesource.capsule.Map;
import mb.flowspec.controlflow.TransferFunctionAppl;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

@TypeSystemReference(Types.class)
public class TransferFunction extends RootNode {
    private ArgToVarNode[] patternVariables;
    private Where body;

    public TransferFunction(FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        this(null, frameDescriptor, patternVariables, body);
    }

    public TransferFunction(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor,
        ArgToVarNode[] patternVariables, Where body) {
        super(language, frameDescriptor);
        this.patternVariables = patternVariables;
        this.body = body;
    }

    @Override public Object execute(VirtualFrame frame) {
        for(ArgToVarNode pv : patternVariables) {
            pv.execute(frame);
        }
        return body.execute(frame);
    }

    public IStrategoTerm call(TransferFunctionAppl appl, IStrategoTerm currentNode) {
        try {
            return Types.expectIStrategoTerm(
                Truffle.getRuntime().createCallTarget(this).call((Object[]) appl.args(currentNode)));
        } catch(UnexpectedResultException e) {
            throw new RuntimeException(e);
        }
    }

    public static TransferFunction findFunction(Map.Immutable<Tuple2<String, Integer>, TransferFunction> tfs,
        TransferFunctionAppl appl) {
        return tfs.get(ImmutableTuple2.of(appl.moduleName(), appl.offset()));
    }
}
