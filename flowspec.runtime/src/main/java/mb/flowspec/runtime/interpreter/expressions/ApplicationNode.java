package mb.flowspec.runtime.interpreter.expressions;

import java.util.Arrays;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

import io.usethesource.capsule.Set.Immutable;
import io.usethesource.capsule.Set.Transient;
import mb.flowspec.runtime.interpreter.values.Set;
import org.spoofax.terms.util.TermUtils;

public class ApplicationNode extends ExpressionNode {
    private static ILogger logger = LoggerUtils.logger(ApplicationNode.class);

    private final FunRefNode reference;
    private final ExpressionNode[] arguments;

    public ApplicationNode(FunRefNode reference, ExpressionNode[] arguments) {
        this.reference = reference;
        this.arguments = arguments;
    }

    @SuppressWarnings("unchecked") @Override public Object executeGeneric(VirtualFrame frame) {
        if(reference instanceof LatticeOpRefNode) {
            LatticeOpRefNode opRefNode = (LatticeOpRefNode) reference;
            assert arguments.length == 2;
            Object arg0 = arguments[0].executeGeneric(frame);
            Object arg1 = arguments[1].executeGeneric(frame);
            return opRefNode.function.apply(arg0, arg1);
        } else if(reference instanceof FunRefRefNode) {
            FunRefRefNode opRefNode = (FunRefRefNode) reference;
            // TODO: prepare arguments for function execution...
            Object[] args = Arrays.stream(arguments).map(a -> a.executeGeneric(frame)).toArray();
            return Truffle.getRuntime().createCallTarget(opRefNode.function).call(args);
        } else if(reference instanceof QualRefNode) {
            QualRefNode qualRef = (QualRefNode) reference;
            if(qualRef.modname.length == 1 && qualRef.modname[0].equals("Set")) {
                switch(qualRef.var) {
                    case "fromList":
                        assert arguments.length == 1;
                        Object arg0 = arguments[0].executeGeneric(frame);
                        assert arg0 instanceof IStrategoTerm && TermUtils.isList((IStrategoTerm)arg0);
                        return new Set<>(listTermToSet((IStrategoList) arg0));
                    default:
                        logger.warn("Don't know reference " + qualRef.var);
                }
            } else {
                logger.warn("Don't know Qualifier " + Arrays.toString(qualRef.modname));
            }
        }
        throw new RuntimeException("Application of unknown function: " + reference);
    }

    private static Immutable<IStrategoTerm> listTermToSet(IStrategoList list) {
        final Transient<IStrategoTerm> terms = Transient.of();
        for(IStrategoTerm term : list) {
            terms.__insert(term);
        }
        return terms.freeze();
    }
}
