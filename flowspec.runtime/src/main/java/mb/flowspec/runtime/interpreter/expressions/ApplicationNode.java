package mb.flowspec.runtime.interpreter.expressions;

 import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Arrays;
import java.util.HashSet;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import io.usethesource.capsule.Set.Immutable;
import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.values.Set;
import mb.nabl2.terms.IListTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ListTerms;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class ApplicationNode extends ExpressionNode {
    private static ILogger logger = LoggerUtils.logger(ApplicationNode.class);
    
    private final RefNode reference;
    private final ExpressionNode[] arguments;

    public ApplicationNode(RefNode reference, ExpressionNode[] arguments) {
        this.reference = reference;
        this.arguments = arguments;
    }

    public static IMatcher<ApplicationNode> match(FrameDescriptor frameDescriptor) {
        return M.appl2("Appl", RefNode.matchRef(frameDescriptor), M.listElems(ExpressionNode.matchExpr(frameDescriptor)), (appl, reference, expr) -> {
            return new ApplicationNode(reference, expr.toArray(new ExpressionNode[expr.size()]));
        });
    }

    public void init(InitValues initValues) {
        for (ExpressionNode argument : arguments) {
            argument.init(initValues);
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (reference instanceof QualRefNode) {
            QualRefNode qualRef = (QualRefNode) reference;
            if (qualRef.modname.length == 1 && qualRef.modname[0].equals("Set")) {
                switch (qualRef.var) {
                    case "fromList":
                        assert arguments.length == 0;
                        Object arg0 = arguments[0].executeGeneric(frame);
                        assert arg0 instanceof IListTerm;
                        java.util.Set<ITerm> list = listTermToSet((IListTerm) arg0);
                        Immutable<ITerm> set = Immutable.of();
                        set = set.__insertAll(list);
                        return new Set<>(set);
                    default:
                        logger.warn("Don't know reference " + qualRef.var);
                }
            } else {
                logger.warn("Don't know Qualifier " + Arrays.toString(qualRef.modname));
            }
        }
        return null;
    }
    
    private static java.util.Set<ITerm> listTermToSet(IListTerm list) {
        final HashSet<ITerm> terms = new HashSet<>();
        while(list != null) {
            list = list.match(ListTerms.<IListTerm>cases(
                // @formatter:off
                cons -> {
                    terms.add(cons.getHead());
                    return cons.getTail();
                },
                nil -> {
                    return null;
                },
                var -> {
                    throw new IllegalArgumentException("Cannot convert specialized terms to Stratego.");
                }
                // @formatter:on
            ));
        }
        return terms;
    }
}
