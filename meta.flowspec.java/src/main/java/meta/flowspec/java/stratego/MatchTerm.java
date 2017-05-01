package meta.flowspec.java.stratego;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public class MatchTerm {
    public static Optional<IStrategoInt> sinteger(IStrategoTerm term) {
        if (term.getTermType() == IStrategoTerm.INT) {
            return Optional.of(((IStrategoInt) term));
        }
        return Optional.empty();
    }

    public static Optional<Integer> integer(IStrategoTerm term) {
        return sinteger(term).map(IStrategoInt::intValue);
    }

    public static Optional<IStrategoReal> sreal(IStrategoTerm term) {
        if (term.getTermType() == IStrategoTerm.REAL) {
            return Optional.of(((IStrategoReal) term));
        }
        return Optional.empty();
    }

    public static Optional<Double> real(IStrategoTerm term) {
        return sreal(term).map(IStrategoReal::realValue);
    }

    public static Optional<IStrategoString> sstring(IStrategoTerm term) {
        if (term.getTermType() == IStrategoTerm.STRING) {
            return Optional.of(((IStrategoString) term));
        }
        return Optional.empty();
    }

    public static Optional<String> string(IStrategoTerm term) {
        return sstring(term).map(IStrategoString::stringValue);
    }

    public static Optional<IStrategoAppl> application(IStrategoTerm term) {
        if (term.getTermType() == IStrategoTerm.APPL) {
            return Optional.of(((IStrategoAppl) term));
        }
        return Optional.empty();
    }

    public static Optional<IStrategoAppl> application(IStrategoConstructor cons, IStrategoTerm term) {
        return application(term).filter(appl -> appl.getConstructor().equals(cons));
    }

    public static Optional<IStrategoList> slist(IStrategoTerm term) {
        if (term.getTermType() == IStrategoTerm.LIST) {
            return Optional.of((IStrategoList) term);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<List<IStrategoTerm>> list(IStrategoTerm term) {
        return slist(term).map(l -> StreamSupport.stream(l.spliterator(), false).collect(Collectors.toList()));
    }

    public static Optional<IStrategoTuple> tuple(IStrategoTerm term) {
        if (term.getTermType() == IStrategoTerm.TUPLE) {
            return Optional.of((IStrategoTuple) term);
        } else {
            return Optional.empty();
        }
    }
    
    public static Optional<IStrategoTerm[]> applChildren(IStrategoConstructor cons, IStrategoTerm term) {
        return application(term).filter(appl -> appl.getConstructor().equals(cons)).map(IStrategoAppl::getAllSubterms);
    }
}
