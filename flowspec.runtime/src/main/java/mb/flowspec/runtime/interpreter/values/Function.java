package mb.flowspec.runtime.interpreter.values;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import mb.flowspec.runtime.interpreter.Context;
import mb.flowspec.runtime.interpreter.InitValues;
import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.runtime.interpreter.expressions.ExpressionNode;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@TypeSystemReference(Types.class)
public class Function extends RootNode {
    private final ArgToVarNode[] arguments;
    private final ExpressionNode body;

    public Function(FrameDescriptor frameDescriptor, ArgToVarNode[] arguments, ExpressionNode body) {
        this(null, frameDescriptor, arguments, body);
    }

    public Function(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor, ArgToVarNode[] arguments,
            ExpressionNode body) {
        super(language, frameDescriptor);
        this.arguments = arguments;
        this.body = body;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        for (ArgToVarNode pv : arguments) {
            pv.execute(frame);
        }
        return body.executeGeneric(frame);
    }

    public void init(InitValues initValues) {
        body.init(initValues);
    }

    public static IMatcher<Function> match(TruffleLanguage<Context> object) {
        return null;
    }

    public static IMatcher<Function> match() {
        return match(null);
    }

    public static IMatcher<Function> matchBinary(String op, TruffleLanguage<Context> language) {
        return (term, unifier) -> 
                M.appl3(op, M.stringValue(), M.stringValue(), M.term(), (appl, left, right, body) -> {
                    FrameDescriptor frameDescriptor = new FrameDescriptor();
                    ArgToVarNode[] args = new ArgToVarNode[] { ArgToVarNode.of(frameDescriptor, 0, left),
                            ArgToVarNode.of(frameDescriptor, 1, right) };
                    return ExpressionNode.matchExpr(frameDescriptor).match(body, unifier)
                            .map(b -> new Function(language, frameDescriptor, args, b));
                })
                .flatMap(i -> i)
                .match(term, unifier);
    }

    public static IMatcher<Function> matchLUB() {
        return matchBinary("Lub", null);
    }
    
    public static IMatcher<Function> matchNullary(TruffleLanguage<Context> language) {
        FrameDescriptor frameDescriptor = new FrameDescriptor();
        return ExpressionNode.matchExpr(frameDescriptor).map(b -> new Function(language, frameDescriptor, new ArgToVarNode[0], b));
    }

    public static IMatcher<Function> matchNullary() {
        return matchNullary(null);
    }
}
