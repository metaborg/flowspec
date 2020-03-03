package mb.flowspec.primitives;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.ImmutableTransferFunctionAppl;
import mb.flowspec.controlflow.TransferFunctionAppl;
import mb.flowspec.terms.M;
import mb.flowspec.terms.TermIndexed;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public class RegisterAppl extends Strategy {
    private final Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls;

    RegisterAppl(Map<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls) {
        this.tfAppls = tfAppls;
    }

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm nodeArg,
        IStrategoTerm propNameArg, IStrategoTerm modNameArg, IStrategoTerm offsetArg, IStrategoTerm argsArg) {
        if(!(nodeArg instanceof ICFGNode && TermUtils.isString(propNameArg)
            && TermUtils.isString(modNameArg) && TermUtils.isInt(offsetArg)
            && TermUtils.isList(argsArg))) {
            return null;
        }
        final ICFGNode node = (ICFGNode) nodeArg;
        final String propName = M.string(propNameArg);
        final String modName = M.string(modNameArg);
        final int offset = M.integer(offsetArg);
        final IStrategoList argsList = M.list(argsArg);
        final List<IStrategoTerm> args = Arrays.asList(TermIndexed.excludeTermIndexFromEqual(argsList.getAllSubterms()));

        this.tfAppls.put(ImmutableTuple2.of(node, propName), ImmutableTransferFunctionAppl.of(modName, offset, args));
        return current;
    }
}
