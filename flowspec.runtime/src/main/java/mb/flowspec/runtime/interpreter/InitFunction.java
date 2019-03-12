package mb.flowspec.runtime.interpreter;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.controlflow.TransferFunctionAppl;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;

public class InitFunction extends TransferFunction {
    public InitFunction(FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        this(null, frameDescriptor, patternVariables, body);
    }

    public InitFunction(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor,
        ArgToVarNode[] patternVariables, Where body) {
        super(language, frameDescriptor, patternVariables, body);
    }

    @Override public IStrategoTerm call(TransferFunctionAppl appl, IStrategoTerm currentNode) {
        try {
            return Types.expectIStrategoTerm(Truffle.getRuntime().createCallTarget(this).call(appl.args()));
        } catch(UnexpectedResultException e) {
            throw new RuntimeException(e);
        }
    }
}
