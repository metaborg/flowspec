package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

public class QualRefNode extends FunRefNode {
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

    public IMatcher<QualRefNode> match() {
        return M.appl2("QualRef", M.stringValue(), M.stringValue(), (appl, modname, var) -> {
            return new QualRefNode(modname, var);
        });
    }

    @Override
    public void init(InitValues initValues) {}
}
