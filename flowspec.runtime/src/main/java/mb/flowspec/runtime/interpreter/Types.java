package mb.flowspec.runtime.interpreter;

import static mb.nabl2.terms.build.TermBuild.B;

import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.interpreter.expressions.BooleanLiteralNode;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.interpreter.values.Name;
import mb.flowspec.runtime.interpreter.values.Set;
import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.IIntTerm;
import mb.nabl2.terms.IStringTerm;
import mb.nabl2.terms.ITerm;

@TypeSystem({Function.class, Set.class, TermIndex.class, Name.class, CFGNode.class, ITerm.class})
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
        assert isString(value) : "Types.asString: String or IStringTerm expected";
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

    public static boolean isBoolean(Object value) {
        return value instanceof Boolean || value instanceof IApplTerm && (value == BooleanLiteralNode.TRUE_TERM || value == BooleanLiteralNode.FALSE_TERM);
    }

    public static boolean asBoolean(Object value) {
        assert isBoolean(value) : "Types.asInt: boolean or IApplTerm True() or False() expected";
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return ((IApplTerm) value) == BooleanLiteralNode.TRUE_TERM;
        }
    }

    public static boolean expectBoolean(Object value) throws UnexpectedResultException {
        if (isBoolean(value)) {
            return asBoolean(value);
        }
        throw new UnexpectedResultException(value);
    }

    public static boolean isITerm(Object value) {
        return value instanceof ITerm || value instanceof Integer || value instanceof String;
    }

    public static ITerm asITerm(Object value) {
        assert isITerm(value) : "Types.asInt: int or IIntTerm expected";
        if (isInteger(value)) {
            return B.newInt(asInteger(value));
        } else if (isString(value)) {
            return B.newString(asString(value));
        } else {
            return (ITerm) value;
        }
    }

    public static ITerm expectITerm(Object value) throws UnexpectedResultException {
        if (value instanceof ITerm) {
            return (ITerm) value;
        }
        if (isInteger(value)) {
            return B.newInt(asInteger(value));
        }
        if (isString(value)) {
            return B.newString(asString(value));
        }
        if (isBoolean(value)) {
            return asBoolean(value) ? BooleanLiteralNode.TRUE_TERM : BooleanLiteralNode.FALSE_TERM;
        }
        throw new UnexpectedResultException(value);
    }
}
