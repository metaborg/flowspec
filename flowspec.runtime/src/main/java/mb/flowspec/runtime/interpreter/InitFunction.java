package mb.flowspec.runtime.interpreter;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;
import mb.nabl2.controlflow.terms.TransferFunctionAppl;
import mb.nabl2.terms.ITerm;

public class InitFunction extends TransferFunction {
    public InitFunction(FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        this(null, frameDescriptor, patternVariables, body);
    }

    public InitFunction(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        super(language, frameDescriptor, patternVariables, body);
    }

    @Override
    public ITerm call(TransferFunctionAppl appl, ITerm currentNode) {
        try {
            return Types.expectITerm(Truffle.getRuntime().createCallTarget(this).call(appl.args()));
        } catch (UnexpectedResultException e) {
            throw new RuntimeException(e);
        }
    }
}
