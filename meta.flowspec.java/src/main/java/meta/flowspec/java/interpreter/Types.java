package meta.flowspec.java.interpreter;

import org.metaborg.meta.nabl2.terms.IIntTerm;
import org.metaborg.meta.nabl2.terms.IStringTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.generic.TB;

import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.values.Function;
import meta.flowspec.java.interpreter.values.Set;

@TypeSystem({boolean.class, Function.class, Set.class, ITerm.class})
public abstract class Types {
    public static boolean isInteger(Object value) {
        return value instanceof Integer || value instanceof IIntTerm;
    }

    public static int asInteger(Object value) {
        assert isInteger(value) : "Types.asInt: int or IIntTerm expected";
        if (value instanceof Integer) {
            return (int) value;
        } else {
            return ((IIntTerm) value).getValue();
        }
    }

    public static int expectInteger(Object value) throws UnexpectedResultException {
        if (isInteger(value)) {
            return asInteger(value);
        }
        throw new UnexpectedResultException(value);
    }
    
    public static boolean isString(Object value) {
        return value instanceof String || value instanceof IStringTerm;
    }

    public static String asString(Object value) {
        assert isString(value) : "Types.asInt: int or IIntTerm expected";
        if (value instanceof String) {
            return (String) value;
        } else {
            return ((IStringTerm) value).getValue();
        }
    }

    public static String expectString(Object value) throws UnexpectedResultException {
        if (isString(value)) {
            return asString(value);
        }
        throw new UnexpectedResultException(value);
    }
    
    public static boolean isITerm(Object value) {
        return value instanceof ITerm || value instanceof Integer || value instanceof String;
    }

    public static ITerm asITerm(Object value) {
        assert isITerm(value) : "Types.asInt: int or IIntTerm expected";
        if (isInteger(value)) {
            return TB.newInt(asInteger(value));
        } else if (isString(value)) {
            return TB.newString(asString(value));
        } else {
            return (ITerm) value;
        }
    }
    
    public static ITerm expectITerm(Object value) throws UnexpectedResultException {
        if (value instanceof ITerm) {
            return (ITerm) value;
        }
        if (isInteger(value)) {
            return TB.newInt(asInteger(value));
        }
        if (isString(value)) {
            return TB.newString(asString(value));
        }
        throw new UnexpectedResultException(value);
    }
}
