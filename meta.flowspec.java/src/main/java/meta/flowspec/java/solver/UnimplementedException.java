package meta.flowspec.java.solver;

public class UnimplementedException extends Exception {

    public UnimplementedException(String string) {
        super("Unimplemented: " + string);
    }

}
