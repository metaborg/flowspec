package mb.flowspec.runtime.interpreter;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.runtime.interpreter.expressions.BooleanLiteralNode;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Name;
import mb.flowspec.terms.B;
import mb.nabl2.terms.stratego.TermIndex;

@TypeSystem({ Function.class, ISet.class, IMap.class, TermIndex.class, Name.class, ICFGNode.class,
    IStrategoTerm.class })
public abstract class Types {
    public static Object typeSpecializeTerm(Object value) {
        if(!(value instanceof IStrategoTerm)) {
            return value;
        }
        if(isInteger(value)) {
            return asInteger(value);
        }
        if(isBoolean(value)) {
            return asBoolean(value);
        }
        // NB: don't specialize to String. That loses annotations from Term Strings from the AST. 
        return value;
    }

    public static boolean isInteger(Object value) {
        return value instanceof Integer || value instanceof IStrategoInt;
    }

    public static int asInteger(Object value) {
        assert isInteger(value) : "Types.asInt: int or IIntTerm expected";
        if(value instanceof Integer) {
            return (int) value;
        } else {
            return ((IStrategoInt) value).intValue();
        }
    }

    public static int expectInteger(Object value) throws UnexpectedResultException {
        if(isInteger(value)) {
            return asInteger(value);
        }
        throw new UnexpectedResultException(value);
    }

    public static boolean isString(Object value) {
        return value instanceof String || value instanceof IStrategoString;
    }

    public static String asString(Object value) {
        assert isString(value) : "Types.asString: String or IStringTerm expected";
        if(value instanceof String) {
            return (String) value;
        } else {
            return ((IStrategoString) value).stringValue();
        }
    }

    public static String expectString(Object value) throws UnexpectedResultException {
        if(isString(value)) {
            return asString(value);
        }
        throw new UnexpectedResultException(value);
    }

    public static boolean isBoolean(Object value) {
        return value instanceof Boolean || BooleanLiteralNode.isTrueTerm(value) || BooleanLiteralNode.isFalseTerm(value);
    }

    public static boolean asBoolean(Object value) {
        assert isBoolean(value) : "Types.asBoolean: boolean or IApplTerm True() or False() expected";
        if(value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return BooleanLiteralNode.isTrueTerm(value);
        }
    }

    public static boolean expectBoolean(Object value) throws UnexpectedResultException {
        if(isBoolean(value)) {
            return asBoolean(value);
        }
        throw new UnexpectedResultException(value);
    }

    public static boolean isIStrategoTerm(Object value) {
        return value instanceof IStrategoTerm || value instanceof Integer || value instanceof String || value instanceof Boolean;
    }

    public static IStrategoTerm asIStrategoTerm(Object value) {
        if(value instanceof IStrategoTerm) {
            return (IStrategoTerm) value;
        }
        if(isInteger(value)) {
            return B.integer(asInteger(value));
        }
        if(isString(value)) {
            return B.string(asString(value));
        }
        if(isBoolean(value)) {
            return BooleanLiteralNode.booleanToTerm(asBoolean(value));
        }
        return (IStrategoTerm) value;
    }

    public static IStrategoTerm expectIStrategoTerm(Object value) throws UnexpectedResultException {
        if(isIStrategoTerm(value)) {
            return asIStrategoTerm(value);
        }
        throw new UnexpectedResultException(value);
    }
}
