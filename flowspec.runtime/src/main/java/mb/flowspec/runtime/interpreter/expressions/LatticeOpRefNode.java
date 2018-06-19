package mb.flowspec.runtime.interpreter.expressions;

import java.util.function.BiFunction;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.solver.UserDefinedLattice;

public class LatticeOpRefNode extends FunRefNode {
    public final LatticeOp op;
    public final String name;
    @SuppressWarnings("rawtypes")
    public BiFunction function;

    public LatticeOpRefNode(LatticeOp op, String name) {
        this.op = op;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(InitValues initValues) {
        @SuppressWarnings("rawtypes")
        UserDefinedLattice lattice = initValues.lattices().get(name);
        switch(this.op) {
        case Lub:
            function = lattice::lub;
            break;
        case Glb:
            function = lattice::glb;
            break;
        case Geq:
            function = lattice::geq;
            break;
        case Leq:
            function = lattice::leq;
            break;
        case NLeq:
            function = lattice::nleq;
            break;
        default:
            break;
        }
    }

    public enum LatticeOp {
        Lub,
        Glb,
        Leq,
        Geq,
        NLeq
    }
}
