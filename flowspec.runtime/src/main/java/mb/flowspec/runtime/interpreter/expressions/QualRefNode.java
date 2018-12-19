package mb.flowspec.runtime.interpreter.expressions;

public class QualRefNode implements FunRefNode {
    public final String[] modname;
    public final String var;

    public QualRefNode(String modname, String var) {
        this.modname = modname.split("/");
        this.var = var;
    }

    @Override
    public String toString() {
        return "QualRefNode [modname=" + String.join("/", modname) + ", var=" + var + "]";
    }
}
