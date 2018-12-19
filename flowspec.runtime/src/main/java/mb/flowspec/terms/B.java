package mb.flowspec.terms;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.terms.StrategoAppl;
import org.spoofax.terms.StrategoConstructor;
import org.spoofax.terms.StrategoInt;
import org.spoofax.terms.StrategoString;
import org.spoofax.terms.StrategoTuple;
import org.spoofax.terms.TermFactory;

import mb.nabl2.stratego.StrategoBlob;

public class B {
    @SuppressWarnings("deprecation")
    public static IStrategoList EMPTY_LIST = TermFactory.EMPTY_LIST;

    public static IStrategoAppl appl(String cons, IStrategoTerm... children) {
        return new StrategoAppl(new StrategoConstructor(cons, children.length), children, EMPTY_LIST,
            IStrategoTerm.SHARABLE);
    }

    public static IStrategoTuple tuple(IStrategoTerm... children) {
        return new StrategoTuple(children, EMPTY_LIST, IStrategoTerm.SHARABLE);
    }

    public static IStrategoString string(String value) {
        return new StrategoString(value, EMPTY_LIST, IStrategoTerm.SHARABLE);
    }

    public static IStrategoInt integer(int value) {
        return new StrategoInt(value, IStrategoTerm.SHARABLE);
    }

    public static IStrategoList list(IStrategoTerm... terms) {
        return new StrategoArrayList(terms);
    }

    public static IStrategoTerm blob(Object blob) {
        return new StrategoBlob(blob);
    }
}
