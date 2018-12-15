package mb.flowspec.runtime.interpreter.values;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.Context;
import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.runtime.interpreter.expressions.ExpressionNode;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;
import mb.flowspec.runtime.solver.ParseException;
import mb.flowspec.runtime.solver.Type;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;

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

    public static IMatcher<ImmutableTuple2<String, Function>> match(TruffleLanguage<Context> language) {
        return (term, unifier) -> 
                Optional.of(
                    M.appl3("FunDef", M.stringValue(), M.term(), M.term(), (appl, name, args, body) -> {
                        FrameDescriptor frameDescriptor = new FrameDescriptor();
                        return M.listElems(matchArg(frameDescriptor)).match(args, unifier).flatMap(a -> {
                            ArgToVarNode[] as = new ArgToVarNode[a.size()];
                            final AtomicInteger i = new AtomicInteger(0);
                            a.stream().map(ImmutableTuple2::_1).forEach(s -> {
                                int offset = i.getAndIncrement();
                                as[offset] = ArgToVarNode.of(frameDescriptor, offset, s);
                            });
                            return ExpressionNode.matchExpr(frameDescriptor)
                                .match(body, unifier)
                                .map(b -> ImmutableTuple2.<String, Function>of(name, new Function(language, frameDescriptor, as, b)));
                        });
                    })
                    .flatMap(i -> i)
                    .match(term, unifier)
                    .orElseThrow(() -> new ParseException("Parse error on reading function"))
                );
    }

    private static IMatcher<ImmutableTuple2<String, Type>> matchArg(FrameDescriptor frameDescriptor) {
        return M.appl2("Arg", M.stringValue(), Type.matchType(), (appl, s, t) -> ImmutableTuple2.of(s,t));
    }

    public static IMatcher<ImmutableTuple2<String, Function>> match() {
        return match(null);
    }

    public static IMatcher<Function> matchBinary(String op, TruffleLanguage<Context> language) {
        return (term, unifier) -> 
                Optional.of(
                    M.appl3(op, M.stringValue(), M.stringValue(), M.term(), (appl, left, right, body) -> {
                        FrameDescriptor frameDescriptor = new FrameDescriptor();
                        ArgToVarNode[] args = new ArgToVarNode[] { ArgToVarNode.of(frameDescriptor, 0, left),
                                ArgToVarNode.of(frameDescriptor, 1, right) };
                        return ExpressionNode.matchExpr(frameDescriptor).match(body, unifier)
                                .map(b -> new Function(language, frameDescriptor, args, b));
                    })
                    .flatMap(i -> i)
                    .match(term, unifier)
                    .orElseThrow(() -> new ParseException("Parse error on reading binary operator function"))
                );
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
