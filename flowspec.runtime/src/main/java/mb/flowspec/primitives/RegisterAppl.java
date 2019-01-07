package mb.flowspec.primitives;

import java.util.Arrays;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import io.usethesource.capsule.Map.Transient;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.ImmutableTransferFunctionAppl;
import mb.flowspec.controlflow.TransferFunctionAppl;
import mb.flowspec.terms.M;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public class RegisterAppl extends Strategy {
    private final Transient<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls;

    RegisterAppl(Transient<Tuple2<ICFGNode, String>, TransferFunctionAppl> tfAppls) {
        this.tfAppls = tfAppls;
    }

    @Override public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm nodeArg,
        IStrategoTerm propNameArg, IStrategoTerm modNameArg, IStrategoTerm offsetArg, IStrategoTerm argsArg) {
        if(!(nodeArg instanceof ICFGNode && propNameArg instanceof IStrategoString
            && modNameArg instanceof IStrategoString && offsetArg instanceof IStrategoInt
            && argsArg instanceof IStrategoList)) {
            return null;
        }
        final ICFGNode node = (ICFGNode) nodeArg;
        final String propName = M.string(propNameArg);
        final String modName = M.string(modNameArg);
        final int offset = M.integer(offsetArg);
        final IStrategoList argsList = M.list(argsArg);
        final List<IStrategoTerm> args = Arrays.asList(argsList.getAllSubterms());

        this.tfAppls.__put(ImmutableTuple2.of(node, propName), ImmutableTransferFunctionAppl.of(modName, offset, args));
        return current;
    }
}
