package mb.flowspec.runtime.solver;

import java.rmi.activation.UnknownObjectException;

import com.oracle.truffle.api.Truffle;

import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.lattice.CompleteLattice;

public final class UserDefinedLattice<E> implements CompleteLattice<E> {
    public final E top_object;
    public final Function lub;

    UserDefinedLattice(E top_object, Function lub) {
        this.top_object = top_object;
        this.lub = lub;
    }

    public E top() {
        return top_object;
    }

    public E bottom() {
        throw new RuntimeException(new UnknownObjectException("bottom not read from FlowSpec definition"));
    }

    public E glb(E one, E other) {
        throw new RuntimeException(new UnknownObjectException("glb not read from FlowSpec definition"));
    }

    @SuppressWarnings("unchecked")
    public E lub(E one, E other) {
        return (E) Truffle.getRuntime().createCallTarget(lub)
                .call(new Object[] {one, other});
    }
}