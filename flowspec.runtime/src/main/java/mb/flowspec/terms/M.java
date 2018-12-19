package mb.flowspec.terms;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.metaborg.util.functions.Function0;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public class M {
    public static IStrategoAppl appl(IStrategoTerm term) {
        assertThat(term, instanceOf(IStrategoAppl.class));
        return (IStrategoAppl) term;
    }

    public static IStrategoAppl appl(IStrategoTerm term, int arity) {
        final IStrategoAppl appl = appl(term);
        assertEquals(arity, appl.getSubtermCount());
        return appl;
    }

    public static IStrategoAppl appl(IStrategoTerm term, String cons, int arity) {
        IStrategoAppl appl = appl(term, arity);
        assertEquals(cons, appl.getName());
        return appl;
    }

    public static IStrategoTuple tuple(IStrategoTerm term) {
        assertThat(term, instanceOf(IStrategoTuple.class));
        return (IStrategoTuple) term;
    }

    public static IStrategoTuple tuple(IStrategoTerm term, int arity) {
        final IStrategoTuple tuple = tuple(term);
        assertEquals(arity, tuple.getSubtermCount());
        return tuple;
    }

    public static IStrategoList list(IStrategoTerm term) {
        assertThat(term, instanceOf(IStrategoList.class));
        return (IStrategoList) term;
    }

    public static String string(IStrategoTerm term) {
        assertThat(term, instanceOf(IStrategoString.class));
        return ((IStrategoString) term).stringValue();
    }

    public static int integer(IStrategoTerm term) {
        assertThat(term, instanceOf(IStrategoInt.class));
        return ((IStrategoInt) term).intValue();
    }

    public static IStrategoTerm at(IStrategoAppl appl, int n) {
        return appl.getSubterm(n);
    }

    public static IStrategoTerm at(IStrategoTuple tuple, int n) {
        return tuple.getSubterm(n);
    }

    public static IStrategoTerm at(IStrategoList list, int n) {
        return list.getSubterm(n);
    }

    public static <R> Optional<R> maybe(Function0<R> matcher) {
        final R result;
        try {
            result = matcher.apply();
        } catch(AssertionError | ClassCastException e) {
            return Optional.empty();
        }
        return Optional.of(result);
    }
}
