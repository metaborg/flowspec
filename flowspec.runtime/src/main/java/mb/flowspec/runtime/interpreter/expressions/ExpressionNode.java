package mb.flowspec.runtime.interpreter.expressions;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.runtime.interpreter.Types;
import mb.flowspec.runtime.interpreter.TypesGen;
import mb.flowspec.runtime.interpreter.values.IMap;
import mb.flowspec.runtime.interpreter.values.ISet;
import mb.flowspec.runtime.interpreter.values.Name;
import mb.nabl2.terms.stratego.TermIndex;

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

    public ICFGNode executeICFGNode(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectICFGNode(executeGeneric(frame));
    }

    public IStrategoTerm executeIStrategoTerm(VirtualFrame frame) throws UnexpectedResultException {
        return Types.expectIStrategoTerm(executeGeneric(frame));
    }

    @SuppressWarnings("unchecked")
    public ISet<IStrategoTerm> executeISet(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectISet(executeGeneric(frame));
    }

    @SuppressWarnings("unchecked")
    public IMap<IStrategoTerm, IStrategoTerm> executeIMap(VirtualFrame frame) throws UnexpectedResultException {
        return TypesGen.expectIMap(executeGeneric(frame));
    }
}
