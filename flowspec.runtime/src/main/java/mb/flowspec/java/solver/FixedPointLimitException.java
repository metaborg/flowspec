package mb.flowspec.java.solver;

public class FixedPointLimitException extends Exception {
    private final String prop;
    private final int limit;

    public FixedPointLimitException(String prop, int fixpointLimit) {
        this.prop = prop;
        this.limit = fixpointLimit;
    }

    @Override
    public String getMessage() {
        return "Fixed point computation on dataflow reached limit '" + limit + "' on property '" + prop + "'. Suspected infinite loop. ";
    }
}
