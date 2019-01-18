package mb.flowspec.terms;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoAppl;
import org.spoofax.terms.StrategoInt;
import org.spoofax.terms.StrategoString;
import org.spoofax.terms.StrategoTuple;

import mb.nabl2.stratego.StrategoBlob;

public class B {
    private final ITermFactory tf;

    public B(ITermFactory tf) {
        this.tf = tf;
    }

    public IStrategoAppl applShared(String cons, IStrategoTerm... children) {
        return tf.makeAppl(tf.makeConstructor(cons, children.length), children, null);
    }

    public IStrategoConstructor consShared(String cons, int children) {
        return tf.makeConstructor(cons, children);
    }

    public IStrategoTuple tupleShared(IStrategoTerm... children) {
        return tf.makeTuple(children);
    }

    public IStrategoString stringShared(String value) {
        return tf.makeString(value);
    }

    public IStrategoInt integerShared(int value) {
        return tf.makeInt(value);
    }

    public static IStrategoAppl appl(IStrategoConstructor cons, IStrategoTerm... children) {
        assert cons.getArity() == children.length : "Expected constructor with arity " + children.length + ", but got arity " + cons.getArity();
        return new StrategoAppl(cons, children, null, IStrategoTerm.SHARABLE);
    }

    public static IStrategoTuple tuple(IStrategoTerm... children) {
        return new StrategoTuple(children, null, IStrategoTerm.SHARABLE);
    }

    public static IStrategoString string(String value) {
        return new StrategoString(value, null, IStrategoTerm.SHARABLE);
    }

    public static IStrategoInt integer(int value) {
        return new StrategoInt(value, null, IStrategoTerm.SHARABLE);
    }

    public static IStrategoList list(IStrategoTerm... terms) {
        return new StrategoArrayList(terms);
    }

    public static IStrategoTerm blob(Object blob) {
        return new StrategoBlob(blob);
    }
}
