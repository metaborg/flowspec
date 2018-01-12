package meta.flowspec.java.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

public class QualRefNode extends RefNode {
    public final String[] modname;
    public final String var;

    public QualRefNode(String modname, String var) {
        this.modname = modname.split("/");
        this.var = var;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // TODO Auto-generated method stub
        return null;
    }

}
