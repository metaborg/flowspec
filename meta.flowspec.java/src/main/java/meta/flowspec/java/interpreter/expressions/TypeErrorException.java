package meta.flowspec.java.interpreter.expressions;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class TypeErrorException extends RuntimeException {

    public TypeErrorException(UnexpectedResultException e) {
        super(e);
    }

}
