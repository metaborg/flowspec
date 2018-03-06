package mb.flowspec.java.solver;

import io.usethesource.capsule.BinaryRelation;

public class CyclicGraphException extends Exception {
    public final BinaryRelation.Immutable<?, ?> cycle;

    public CyclicGraphException(BinaryRelation.Immutable<?, ?> cycle) {
        this.cycle = cycle;
    }

    @Override
    public String getMessage() {
        return "Cyclic dependency between the following: " + cycle.toString();
    }
}
