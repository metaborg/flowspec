package meta.flowspec.java.interpreter.expressions;

import java.util.Optional;

import org.metaborg.meta.nabl2.controlflow.terms.CFGNode;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.Terms.IMatcher;
import org.metaborg.meta.nabl2.terms.Terms.M;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

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

    public static IMatcher<ExpressionNode> matchExpr(FrameDescriptor frameDescriptor, ISolution solution) {
        return term -> Optional.of(M.cases(
            TermNode.match(frameDescriptor, solution),
            RefNode.matchRef(frameDescriptor),
            PropNode.match(frameDescriptor, solution),
            ExtPropNode.match(frameDescriptor, solution),
            TupleNode.match(frameDescriptor, solution),
            IntLiteralNode.match(frameDescriptor, solution),
            StringLiteralNode.match(frameDescriptor, solution),
            TypeNode.match(frameDescriptor, solution),
            // TODO Abs/1
            ApplicationNode.match(frameDescriptor, solution),
            IfNode.match(frameDescriptor, solution),
            EqualNode.match(frameDescriptor, solution),
            NotEqualNode.match(frameDescriptor, solution),
            NotNode.match(frameDescriptor, solution),
            PlusNode.match(frameDescriptor, solution),
            // TODO Match/2?
            SetLiteralNode.match(frameDescriptor, solution),
            SetCompNode.match(frameDescriptor, solution),
            TermIndexNode.match(frameDescriptor, solution),
            NaBL2OccurrenceNode.match(frameDescriptor, solution),
            SetUnionNode.match(frameDescriptor, solution),
            SetMinusNode.match(frameDescriptor, solution),
            SetContainsNode.match(frameDescriptor, solution),
            SetIntersectNode.match(frameDescriptor, solution)
        ).match(term)
         .orElseThrow(() -> new ParseException("Parse error on reading expression " + term)));
    }
}
