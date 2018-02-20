package meta.flowspec.java.interpreter.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;

import meta.flowspec.java.interpreter.InitValues;

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

    @Override
    public void init(InitValues initValues) {
        // TODO Auto-generated method stub
        
    }

}
