package meta.flowspec.java.interpreter.expressions;

import java.util.Optional;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import meta.flowspec.java.interpreter.InitValues;
import meta.flowspec.java.interpreter.Types;
import meta.flowspec.java.interpreter.TypesGen;
import meta.flowspec.java.interpreter.values.Name;
import meta.flowspec.java.solver.ParseException;

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
        return TypesGen.expectBoolean(executeGeneric(frame));
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
    public meta.flowspec.java.interpreter.values.Set<ITerm> executeSet(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectSet(executeGeneric(frame));
    }

    public abstract void init(InitValues initValues);

    public static IMatcher<ExpressionNode> matchExpr(FrameDescriptor frameDescriptor) {
        return term -> Optional.of(M.cases(
            TermNode.match(frameDescriptor),
            RefNode.matchRef(frameDescriptor),
            PropNode.match(frameDescriptor),
            ExtPropNode.match(frameDescriptor),
            TupleNode.match(frameDescriptor),
            IntLiteralNode.match(frameDescriptor),
            StringLiteralNode.match(frameDescriptor),
            TypeNode.match(frameDescriptor),
            // TODO Abs/1
            ApplicationNode.match(frameDescriptor),
            IfNode.match(frameDescriptor),
            EqualNode.match(frameDescriptor),
            NotEqualNode.match(frameDescriptor),
            NotNode.match(frameDescriptor),
            PlusNode.match(frameDescriptor),
            // TODO Match/2?
            SetLiteralNode.match(frameDescriptor),
            SetCompNode.match(frameDescriptor),
            TermIndexNode.match(frameDescriptor),
            NaBL2OccurrenceNode.match(frameDescriptor),
            SetUnionNode.match(frameDescriptor),
            SetMinusNode.match(frameDescriptor),
            SetContainsNode.match(frameDescriptor),
            SetIntersectNode.match(frameDescriptor)
        ).match(term)
         .orElseThrow(() -> new ParseException("Parse error on reading expression " + term)));
    }
}
