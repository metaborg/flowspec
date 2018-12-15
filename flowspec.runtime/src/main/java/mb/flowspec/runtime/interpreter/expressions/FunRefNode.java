package mb.flowspec.runtime.interpreter.expressions;

import com.oracle.truffle.api.frame.FrameDescriptor;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.expressions.LatticeOpRefNode.LatticeOp;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import static mb.nabl2.terms.matching.TermMatch.M;

public abstract class FunRefNode {
    public static IMatcher<FunRefNode> matchRef(FrameDescriptor frameDescriptor) {
        return M.cases(
                M.appl1("Ref", M.stringValue(), (appl, string) -> new FunRefRefNode(string)),
                M.appl1("LubOf", M.stringValue(), (appl, string) -> new LatticeOpRefNode(LatticeOp.Lub, string)),
                M.appl1("GlbOf", M.stringValue(), (appl, string) -> new LatticeOpRefNode(LatticeOp.Glb, string)),
                M.appl1("LeqOf", M.stringValue(), (appl, string) -> new LatticeOpRefNode(LatticeOp.Leq, string)),
                M.appl1("GeqOf", M.stringValue(), (appl, string) -> new LatticeOpRefNode(LatticeOp.Geq, string)),
                M.appl1("NLeqOf", M.stringValue(), (appl, string) -> new LatticeOpRefNode(LatticeOp.NLeq, string)),
                QualRefNode.match()
        );
    }

    public abstract void init(InitValues initValues);
}
