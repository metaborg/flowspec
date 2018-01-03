package meta.flowspec.java.interpreter.expressions;

import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.IControlFlowGraph;
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
import meta.flowspec.java.interpreter.locals.ReadVarNode;
import meta.flowspec.java.interpreter.values.Tuple;

@TypeSystemReference(Types.class)
public abstract class ExpressionNode extends Node {
    /**
     * The execute method when no specialisation is possible. This is the most general case,
     * therefore it must be provided by all subclasses.
     */
    public abstract Object executeGeneric(VirtualFrame frame);

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectInteger(executeGeneric(frame));
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectBoolean(executeGeneric(frame));
    }
    
    public Tuple executeTuple(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectTuple(executeGeneric(frame));
    }
    
    @SuppressWarnings("unchecked")
    public meta.flowspec.java.interpreter.Set<ITerm> executeSet(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectSet(executeGeneric(frame));
    }

    public static IMatcher<ExpressionNode> matchExpr(FrameDescriptor frameDescriptor, IControlFlowGraph<ICFGNode> cfg) {
        return term -> M.cases(
            // TODO Term/1?
            // TODO QualRef/2
            M.appl1("Ref", ReadVarNode.match(frameDescriptor), (appl, rvn) -> rvn),
            ReadPropNode.match(frameDescriptor, cfg),
            TupleNode.match(frameDescriptor, cfg),
            IntLiteralNode.match(frameDescriptor, cfg),
            StringLiteralNode.match(frameDescriptor, cfg),
            TypeNode.match(frameDescriptor, cfg),
            // TODO Abs/1
            // TODO Appl/2
            IfNode.match(frameDescriptor, cfg),
            EqualNode.match(frameDescriptor, cfg),
            NotEqualNode.match(frameDescriptor, cfg),
            PlusNode.match(frameDescriptor, cfg),
            // TODO Match/2?
            SetLiteralNode.match(frameDescriptor, cfg),
            SetCompNode.match(frameDescriptor, cfg),
            SetUnionNode.match(frameDescriptor, cfg),
            SetMinusNode.match(frameDescriptor, cfg),
            SetContainsNode.match(frameDescriptor, cfg),
            SetIntersectNode.match(frameDescriptor, cfg)
        ).match(term);
    }
}
