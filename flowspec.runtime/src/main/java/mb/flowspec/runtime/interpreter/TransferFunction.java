package mb.flowspec.runtime.interpreter;

import static mb.nabl2.terms.matching.TermMatch.M;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;
import mb.nabl2.controlflow.terms.ICFGNode;
import mb.nabl2.controlflow.terms.TransferFunctionAppl;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

@TypeSystemReference(Types.class)
public class TransferFunction extends RootNode {
    private ArgToVarNode[] patternVariables;
    private Where body;

    public TransferFunction(FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        this(null, frameDescriptor, patternVariables, body);
    }

    public TransferFunction(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor, ArgToVarNode[] patternVariables, Where body) {
        super(language, frameDescriptor);
        this.patternVariables = patternVariables;
        this.body = body;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        for (ArgToVarNode pv : patternVariables) {
            pv.execute(frame);
        }
        return body.execute(frame);
    }
    
    public void init(InitValues initValues) {
        body.init(initValues);
    }

    public static IMatcher<TransferFunction> match(TruffleLanguage<Context> language, FrameDescriptor frameDescriptor) {
        return M.cases(
            M.appl2("TransferFunction", ArgToVarNode.matchList(frameDescriptor), Where.match(frameDescriptor),
            (appl, patternVariables, body) -> {
                return new TransferFunction(language, frameDescriptor, patternVariables, body);
            }),
            M.appl2("InitFunction", ArgToVarNode.matchList(frameDescriptor), Where.match(frameDescriptor),
            (appl, patternVariables, body) -> {
                return new InitFunction(language, frameDescriptor, patternVariables, body);
            })
        );
    }

    public static IMatcher<TransferFunction[]> matchList() {
        return M.listElems(
                    M.tuple2(
                        M.integerValue(),
                        (term, unifier) -> TransferFunction.match(null, new FrameDescriptor()).match(term, unifier),
                        (appl, i, tf) -> ImmutableTuple2.of(i,tf)))
                .map(list -> {
                    TransferFunction[] tfs = new TransferFunction[list.size()];
                    for(ImmutableTuple2<Integer, TransferFunction> t2 : list) {
                        tfs[t2._1()] = t2._2();
                    }
                    return tfs;
                });
    }

    public static <N extends ICFGNode> ITerm call(TransferFunctionAppl appl, Map.Immutable<Tuple2<String, Integer>, TransferFunction> tfs, ITerm arg) {
        try {
            return Types.expectITerm(Truffle.getRuntime().createCallTarget(tfs.get(ImmutableTuple2.of(appl.moduleName(), appl.offset()))).call((Object[]) appl.args(arg)));
        } catch (UnexpectedResultException e) {
            throw new RuntimeException(e);
        }
    }
}
