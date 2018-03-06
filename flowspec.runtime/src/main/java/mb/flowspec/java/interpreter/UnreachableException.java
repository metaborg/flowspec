package mb.flowspec.java.interpreter;

public class UnreachableException extends RuntimeException {
    private static final String MESSAGE = "Reached some place thought to be unreachable...";

    public UnreachableException() {
        this(MESSAGE, null);
    }

    public UnreachableException(Exception e) {
        this(MESSAGE, e);
    }

    public UnreachableException(String message, Throwable cause) {
        super(message, cause);
    }
}
