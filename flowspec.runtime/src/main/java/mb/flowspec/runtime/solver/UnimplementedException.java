package mb.flowspec.runtime.solver;

public class UnimplementedException extends RuntimeException {

    public UnimplementedException(String string) {
        super("Unimplemented: " + string);
    }

}
