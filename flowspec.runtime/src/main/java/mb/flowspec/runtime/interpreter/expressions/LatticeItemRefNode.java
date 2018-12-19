package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.Initializable;
import mb.flowspec.runtime.lattice.CompleteLattice;

public class LatticeItemRefNode extends ExprRefNode implements Initializable {
    private LatticeItem item;
    private String name;
    private Object value;

    public LatticeItemRefNode(LatticeItem item, String name) {
        this.item = item;
        this.name = name;
    }

    @Override public Object executeGeneric(VirtualFrame frame) {
        return value;
    }

    @Override public void init(InitValues initValues) {
        @SuppressWarnings("rawtypes") CompleteLattice lattice = initValues.lattices().get(name);
        switch(this.item) {
            case Top:
                value = lattice.top();
                break;
            case Bottom:
                value = lattice.bottom();
                break;
            default:
                break;
        }
    }

    public static enum LatticeItem {
        Top, Bottom
    }
}
