package mb.flowspec.runtime.lattice;

import com.oracle.truffle.api.Truffle;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.solver.UnimplementedException;

public final class UserDefinedLattice implements CompleteLattice<Object> {
    public final Function top_object;
    public final Function bottom_object;
    public final Function lub;

    public UserDefinedLattice(Function top_object, Function bottom_object, Function lub) {
        this.top_object = top_object;
        this.bottom_object = bottom_object;
        this.lub = lub;
    }

    public Object top() {
        return Truffle.getRuntime().createCallTarget(top_object).call(new Object[0]);
    }

    public Object bottom() {
        return Truffle.getRuntime().createCallTarget(bottom_object).call(new Object[0]);
    }

    public Object glb(Object one, Object other) {
        throw new RuntimeException(new UnimplementedException("glb not read from FlowSpec definition"));
    }

    public Object lub(Object one, Object other) {
        return Truffle.getRuntime().createCallTarget(lub)
                .call(new Object[] {one, other});
    }

    @Override
    public void init(InitValues initValues) {
        top_object.init(initValues);
        bottom_object.init(initValues);
        lub.init(initValues);
    }
}