package mb.flowspec.runtime.interpreter.expressions;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Optional;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.runtime.interpreter.TypesGen;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Name;
import mb.flowspec.runtime.solver.ParseException;
import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@TypeSystemReference(Types.class)
public abstract class ExpressionNode extends Node {
    /**
     * The execute method when no specialisation is possible. This is the most general case,
     * therefore it must be provided by all subclasses.
     */
    public abstract Object executeGeneric(VirtualFrame frame);

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return Types.expectInteger(executeGeneric(frame));
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return Types.expectBoolean(executeGeneric(frame));
    }

    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return Types.expectString(executeGeneric(frame));
    }

    public TermIndex executeTermIndex(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectTermIndex(executeGeneric(frame));
    }

    public Name executeName(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectName(executeGeneric(frame));
    }

    public CFGNode executeCFGNode(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectCFGNode(executeGeneric(frame));
    }

    public ITerm executeITerm(VirtualFrame frame) throws UnexpectedResultException {
        return Types.expectITerm(executeGeneric(frame));
    }

    @SuppressWarnings("unchecked")
    public ISet<ITerm> executeISet(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectISet(executeGeneric(frame));
    }

    @SuppressWarnings("unchecked")
    public IMap<ITerm, ITerm> executeIMap(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectIMap(executeGeneric(frame));
    }

    public abstract void init(InitValues initValues);

    public static IMatcher<ExpressionNode> matchExpr(FrameDescriptor frameDescriptor) {
        return (term, unifier) -> Optional.of(M.cases(
            TermNode.match(frameDescriptor),
            ExprRefNode.matchExprRef(frameDescriptor),
            PropNode.match(frameDescriptor),
            ExtPropNode.match(frameDescriptor),
            TupleNode.match(frameDescriptor),
            IntLiteralNode.match(frameDescriptor),
            StringLiteralNode.match(frameDescriptor),
            BooleanLiteralNode.match(frameDescriptor),
            TypeNode.match(frameDescriptor),
            ApplicationNode.match(frameDescriptor),
            IfNode.match(frameDescriptor),
            EqualNode.match(frameDescriptor),
            NotEqualNode.match(frameDescriptor),
            AndNode.match(frameDescriptor),
            OrNode.match(frameDescriptor),
            NotNode.match(frameDescriptor),
            LtNode.match(frameDescriptor),
            LteNode.match(frameDescriptor),
            GtNode.match(frameDescriptor),
            GteNode.match(frameDescriptor),
            AddNode.match(frameDescriptor),
            SubNode.match(frameDescriptor),
            MulNode.match(frameDescriptor),
            DivNode.match(frameDescriptor),
            ModNode.match(frameDescriptor),
            NegNode.match(frameDescriptor),
            MatchNode.match(frameDescriptor),
            SetLiteralNode.match(frameDescriptor),
            SetCompNode.match(frameDescriptor),
            MapLiteralNode.match(frameDescriptor),
            MapCompNode.match(frameDescriptor),
            MapLookupNode.match(frameDescriptor),
            EmptySetOrMapLiteral.match(frameDescriptor),
            TermIndexNode.match(frameDescriptor),
            NaBL2OccurrenceNode.match(frameDescriptor),
            SetUnionNode.match(frameDescriptor),
            SetMinusNode.match(frameDescriptor),
            SetContainsNode.match(frameDescriptor),
            SetIntersectNode.match(frameDescriptor)
        ).match(term, unifier)
         .orElseThrow(() -> new ParseException("Parse error on reading expression " + term)));
    }
}
