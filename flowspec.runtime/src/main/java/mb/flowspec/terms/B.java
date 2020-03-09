package mb.flowspec.terms;

import mb.nabl2.terms.stratego.StrategoBlob;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * A Stratego Term building class. The static methods can build anything but stratego application constructors.
 * Compiled Stratego code uses cached constructors where the objects are compared with object identity, therefore when
 * you build your own, you should also use shared constructors in the factory.
 *
 * <b>N.B.</b> You can build Stratego strings with static methods from the class, but those will not be known to the
 * TermFactory and therefore will not be taken into consideration by Stratego in its new/newname strategies which are
 * supposed to create globally unique strings.
 */
public class B extends org.spoofax.terms.util.B {
    public B(ITermFactory tf) {
        super(tf);
    }

    public static IStrategoTerm blob(Object blob) {
        return new StrategoBlob(blob);
    }
}
