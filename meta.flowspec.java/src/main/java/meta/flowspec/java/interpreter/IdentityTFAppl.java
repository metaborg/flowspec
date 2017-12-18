package meta.flowspec.java.interpreter;

import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.IControlFlowGraph;

public class IdentityTFAppl<S extends ICFGNode> extends TransferFunctionAppl {
    private final String prop;
    private final IControlFlowGraph<S> cfg;

    public IdentityTFAppl(IControlFlowGraph<S> cfg, String prop) {
        super(0, new Object[] {});
        this.prop = prop;
        this.cfg = cfg;
    }

    @SuppressWarnings("unchecked")
    public Object call(TransferFunction[] _tfs, Object arg) {
        return cfg.getProperty((S) arg, prop);
    }
}