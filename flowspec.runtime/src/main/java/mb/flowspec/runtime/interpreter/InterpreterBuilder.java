package mb.flowspec.runtime.interpreter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import org.metaborg.util.Ref;

import com.oracle.truffle.api.frame.FrameDescriptor;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import mb.flowspec.runtime.ImmutableInitValues;
import mb.flowspec.runtime.InitValues;
import mb.flowspec.runtime.Initializable;
import mb.flowspec.runtime.interpreter.expressions.AddNodeGen;
import mb.flowspec.runtime.interpreter.expressions.AndNodeGen;
import mb.flowspec.runtime.interpreter.expressions.ApplicationNode;
import mb.flowspec.runtime.interpreter.expressions.BooleanLiteralNode;
import mb.flowspec.runtime.interpreter.expressions.CompPredicateNode;
import mb.flowspec.runtime.interpreter.expressions.DivNodeGen;
import mb.flowspec.runtime.interpreter.expressions.EmptySetOrMapLiteral;
import mb.flowspec.runtime.interpreter.expressions.EqualNodeGen;
import mb.flowspec.runtime.interpreter.expressions.ExpressionNode;
import mb.flowspec.runtime.interpreter.expressions.ExtPropNode;
import mb.flowspec.runtime.interpreter.expressions.FunRefNode;
import mb.flowspec.runtime.interpreter.expressions.FunRefRefNode;
import mb.flowspec.runtime.interpreter.expressions.GtNodeGen;
import mb.flowspec.runtime.interpreter.expressions.GteNodeGen;
import mb.flowspec.runtime.interpreter.expressions.IfNode;
import mb.flowspec.runtime.interpreter.expressions.IntLiteralNode;
import mb.flowspec.runtime.interpreter.expressions.LatticeItemRefNode;
import mb.flowspec.runtime.interpreter.expressions.LatticeItemRefNode.LatticeItem;
import mb.flowspec.runtime.interpreter.expressions.LatticeOpRefNode;
import mb.flowspec.runtime.interpreter.expressions.LatticeOpRefNode.LatticeOp;
import mb.flowspec.runtime.interpreter.expressions.LtNodeGen;
import mb.flowspec.runtime.interpreter.expressions.LteNodeGen;
import mb.flowspec.runtime.interpreter.expressions.MapCompNode;
import mb.flowspec.runtime.interpreter.expressions.MapLiteralNode;
import mb.flowspec.runtime.interpreter.expressions.MapLookupNode;
import mb.flowspec.runtime.interpreter.expressions.MatchNode;
import mb.flowspec.runtime.interpreter.expressions.ModNodeGen;
import mb.flowspec.runtime.interpreter.expressions.MulNodeGen;
import mb.flowspec.runtime.interpreter.expressions.NaBL2OccurrenceNode;
import mb.flowspec.runtime.interpreter.expressions.NegNodeGen;
import mb.flowspec.runtime.interpreter.expressions.NotEqualNodeGen;
import mb.flowspec.runtime.interpreter.expressions.NotNodeGen;
import mb.flowspec.runtime.interpreter.expressions.OrNodeGen;
import mb.flowspec.runtime.interpreter.expressions.PropNode;
import mb.flowspec.runtime.interpreter.expressions.PropNodeGen;
import mb.flowspec.runtime.interpreter.expressions.QualRefNode;
import mb.flowspec.runtime.interpreter.expressions.SetCompMatchPredicateNode;
import mb.flowspec.runtime.interpreter.expressions.SetCompNode;
import mb.flowspec.runtime.interpreter.expressions.SetContainsNodeGen;
import mb.flowspec.runtime.interpreter.expressions.SetIntersectNodeGen;
import mb.flowspec.runtime.interpreter.expressions.SetLiteralNode;
import mb.flowspec.runtime.interpreter.expressions.SetMinusNodeGen;
import mb.flowspec.runtime.interpreter.expressions.SetUnionNodeGen;
import mb.flowspec.runtime.interpreter.expressions.StringLiteralNode;
import mb.flowspec.runtime.interpreter.expressions.SubNodeGen;
import mb.flowspec.runtime.interpreter.expressions.TermIndexNodeGen;
import mb.flowspec.runtime.interpreter.expressions.TermNode;
import mb.flowspec.runtime.interpreter.expressions.TupleNode;
import mb.flowspec.runtime.interpreter.expressions.TypeNode;
import mb.flowspec.runtime.interpreter.locals.ArgToVarNode;
import mb.flowspec.runtime.interpreter.locals.ReadVarNode;
import mb.flowspec.runtime.interpreter.locals.ReadVarNodeGen;
import mb.flowspec.runtime.interpreter.locals.WriteVarNode;
import mb.flowspec.runtime.interpreter.locals.WriteVarNodeGen;
import mb.flowspec.runtime.interpreter.patterns.AtPatternNode;
import mb.flowspec.runtime.interpreter.patterns.ConsPatternNode;
import mb.flowspec.runtime.interpreter.patterns.IntLiteralPatternNode;
import mb.flowspec.runtime.interpreter.patterns.NilPatternNode;
import mb.flowspec.runtime.interpreter.patterns.PatternNode;
import mb.flowspec.runtime.interpreter.patterns.StringLiteralPatternNode;
import mb.flowspec.runtime.interpreter.patterns.TermPatternNode;
import mb.flowspec.runtime.interpreter.patterns.TuplePatternNode;
import mb.flowspec.runtime.interpreter.patterns.VarPatternNode;
import mb.flowspec.runtime.interpreter.patterns.WildcardPatternNode;
import mb.flowspec.runtime.interpreter.values.Function;
import mb.flowspec.runtime.lattice.CompleteLattice;
import mb.flowspec.runtime.lattice.FullSetLattice;
import mb.flowspec.runtime.lattice.MapLattice;
import mb.flowspec.runtime.lattice.UserDefinedLattice;
import mb.flowspec.runtime.solver.FunctionInfo;
import mb.flowspec.runtime.solver.ImmutableFunctionInfo;
import mb.flowspec.runtime.solver.ImmutableLatticeInfo;
import mb.flowspec.runtime.solver.ImmutableMapType;
import mb.flowspec.runtime.solver.ImmutableMetadata;
import mb.flowspec.runtime.solver.ImmutableSetType;
import mb.flowspec.runtime.solver.ImmutableSimpleType;
import mb.flowspec.runtime.solver.ImmutableStaticInfo;
import mb.flowspec.runtime.solver.ImmutableTupleType;
import mb.flowspec.runtime.solver.ImmutableUserType;
import mb.flowspec.runtime.solver.LatticeInfo;
import mb.flowspec.runtime.solver.MapType;
import mb.flowspec.runtime.solver.Metadata;
import mb.flowspec.runtime.solver.Metadata.Direction;
import mb.flowspec.runtime.solver.SimpleType;
import mb.flowspec.runtime.solver.StaticInfo;
import mb.flowspec.runtime.solver.Type;
import mb.flowspec.runtime.solver.UserType;
import mb.nabl2.controlflow.terms.CFGNode;
import mb.nabl2.scopegraph.terms.Namespace;
import mb.nabl2.solver.ISolution;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.IIntTerm;
import mb.nabl2.terms.IListTerm;
import mb.nabl2.terms.IStringTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ListTerms;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public class InterpreterBuilder {
    protected final ArrayList<Initializable> initializable = new ArrayList<>();
    protected StaticInfo staticInfo = StaticInfo.of();
    protected final Deque<FrameDescriptor> frameStack = new ArrayDeque<>();
    protected String moduleName;

    // Public API

    public InterpreterBuilder add(ITerm term, String moduleName) {
        try {
            this.moduleName = moduleName;
            frameStack.push(new FrameDescriptor());
            this.staticInfo = this.staticInfo.addAll(staticInfo(term));
        } catch(Exception e) {
            throw new IllegalArgumentException("Parse error on reading Static Info", e);
        } finally {
            frameStack.pop();
            this.moduleName = null;
        }
        return this;
    }

    public InterpreterBuilder add(InterpreterBuilder that) {
        this.initializable.addAll(that.initializable);
        this.staticInfo = this.staticInfo.addAll(that.staticInfo);
        return this;
    }

    /**
     * Pass the NaBL2 solution to the interpreter AST so it can save references to the CFG and the
     * resolution result in certain places
     */
    public StaticInfo build(ISolution nabl2solution, Map.Transient<Tuple2<CFGNode, String>, Ref<ITerm>> preProperties) {
        InitValues initValues = ImmutableInitValues
            .of(nabl2solution.config(), nabl2solution.flowSpecSolution().controlFlowGraph(), preProperties,
                nabl2solution.scopeGraph(), nabl2solution.unifier(), nabl2solution.astProperties(),
                staticInfo.functions().functions(), staticInfo.lattices().latticeDefs())
            .withNameResolutionCache(nabl2solution.nameResolutionCache());

        for(Initializable i : initializable) {
            i.init(initValues);
        }
        return staticInfo;
    }

    // Matching functions

    @SuppressWarnings({ "rawtypes", "unchecked" }) private StaticInfo staticInfo(ITerm term) {
        final IApplTerm appl = tuple(term, 3);
        final LatticeInfo latticeInfo = latticeInfo(n(appl, 1));
        final FunctionInfo functionInfo = functionInfo(n(appl, 2));
        final Map.Immutable<String, CompleteLattice> latticeDefs = latticeInfo.latticeDefs();

        final Map.Transient<String, Metadata<?>> propMetadata = Map.Transient.of();
        final BinaryRelation.Transient<String, String> dependsOn = BinaryRelation.Transient.of();

        for(ITerm tfInfoTerm : list(n(appl, 0))) {
            final IApplTerm tfInfoTuple = tuple(tfInfoTerm, 2);
            final String propName = string(n(tfInfoTuple, 0));
            final IApplTerm tfNestedTuple = tuple(n(tfInfoTuple, 1), 3);
            final Type type = type(n(tfNestedTuple, 0));
            final Direction dir = direction(n(tfNestedTuple, 1));
            final List<ITerm> funs = list(n(tfNestedTuple, 2));

            final Map.Transient<Tuple2<String, Integer>, TransferFunction> tfMap = Map.Transient.of();
            for(ITerm funTerm : funs) {
                final IApplTerm funTuple = tuple(funTerm, 2);
                int index = integer(n(funTuple, 0));
                final TransferFunction tf = transferFunction(n(funTuple, 1));
                tfMap.__put(ImmutableTuple2.of(moduleName, index), tf);
            }

            propMetadata.__put(propName, ImmutableMetadata.of(dir, latticeFromType(latticeDefs, type), tfMap.freeze()));
        }

        return ImmutableStaticInfo.of(dependsOn.freeze(), propMetadata.freeze(), functionInfo, latticeInfo);
    }

    private Type type(ITerm term) {
        switch(appl(term).getOp()) {
            case "Tuple": {
                IApplTerm appl = appl(term, 2);
                return ImmutableTupleType.of(type(n(appl, 0)), type(n(appl, 1)));
            }
            case "Map": {
                IApplTerm appl = appl(term, 2);
                return ImmutableMapType.of(type(n(appl, 0)), type(n(appl, 1)));
            }
            case "Set": {
                IApplTerm appl = appl(term, 1);
                return ImmutableSetType.of(type(n(appl, 0)));
            }
            case "UserType": {
                IApplTerm appl = appl(term, 2);
                List<ITerm> typeTerms = list(n(appl, 1));
                Type[] types = new Type[typeTerms.size()];
                int i = 0;
                for(ITerm typeTerm : typeTerms) {
                    types[i] = type(typeTerm);
                    i++;
                }
                return ImmutableUserType.of(string(n(appl, 0)), types);
            }
            default: {
                IApplTerm appl = appl(term, 0);
                return ImmutableSimpleType.of(SimpleType.SimpleTypeEnum.valueOf(appl.getOp()));
            }
        }
    }

    private Direction direction(ITerm term) {
        IApplTerm appl = appl(term, 0);
        switch(appl.getOp()) {
            case "Bw":
                return Direction.Backward;
            case "Fw":
                return Direction.Forward;
        }
        throw new AssertionError("Direction was not equal to Fw or Bw");
    }

    private TransferFunction transferFunction(ITerm term) {
        final IApplTerm appl = appl(term, 2);
        final ArgToVarNode[] args = argList(n(appl, 0));
        final Where body = where(n(appl, 1));
        switch(appl.getOp()) {
            case "TransferFunction":
                return new TransferFunction(null, frameStack.peek(), args, body);
            case "InitFunction":
                return new InitFunction(null, frameStack.peek(), args, body);
        }
        throw new AssertionError("Transfer function was not equal to TransferFunction or InitFunction");
    }

    private Where where(ITerm appl) {
        final IApplTerm whereAppl = appl(appl, "Where", 2);
        final List<ITerm> varList = list(n(whereAppl, 1));
        final WriteVarNode[] writeVars = new WriteVarNode[varList.size()];

        int i = 0;
        for(ITerm varTerm : varList) {
            final IApplTerm varAppl = appl(varTerm, "Binding", 2);
            final String name = string(n(varAppl, 0));
            final ExpressionNode expr = expressionNode(n(varAppl, 1));
            final FrameDescriptor fd = frameStack.peek();
            writeVars[i] = WriteVarNodeGen.create(expr, fd.findOrAddFrameSlot(name));
            i++;
        }

        final ExpressionNode expr = expressionNode(n(whereAppl, 0));
        final Where body = new Where(writeVars, expr);
        return body;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) protected static CompleteLattice
        latticeFromType(Map<String, CompleteLattice> latticeDefs, Type type) {
        // TODO: Replace Map with MayMap and MustMap
        if(type instanceof UserType) {
            UserType utype = (UserType) type;
            return latticeDefs.get(utype.name());
        } else if(type instanceof MapType) {
            MapType mtype = (MapType) type;
            return new MapLattice(latticeFromType(latticeDefs, mtype.value()));
        }
        throw new AssertionError("Type was not UserType or MapType");
    }

    private FunctionInfo functionInfo(ITerm term) {
        Map.Transient<String, Function> map = Map.Transient.of();

        for(ITerm functionTerm : list(term)) {
            final IApplTerm appl = appl(functionTerm, "FunDef", 3);
            final String name = string(n(appl, 0));
            final List<ITerm> argsList = list(n(appl, 1));
            final ITerm bodyTerm = n(appl, 2);

            final FrameDescriptor frameDescriptor = new FrameDescriptor();
            final ArgToVarNode[] args = new ArgToVarNode[argsList.size()];
            int i = 0;
            for(ITerm argTerm : argsList) {
                final IApplTerm arg = appl(argTerm, "Arg", 2);
                args[i] = ArgToVarNode.of(frameDescriptor, i, string(n(arg, 0)));
                // n(arg, 1): Type
                i++;
            }

            frameStack.push(frameDescriptor);
            final ExpressionNode body = expressionNode(bodyTerm);
            frameStack.pop();

            final Function function = new Function(null, frameDescriptor, args, body);

            map.__put(name, function);
        }

        return ImmutableFunctionInfo.of(map.freeze());
    }

    @SuppressWarnings("rawtypes") private LatticeInfo latticeInfo(ITerm term) {
        final Map.Transient<String, CompleteLattice> latticeDefs = Map.Transient.of();
        latticeDefs.__put("MaySet", new FullSetLattice<>());
        latticeDefs.__put("MustSet", new FullSetLattice<>().flip());

        for(ITerm tfTerm : list(term)) {
            final IApplTerm tfTuple = tuple(tfTerm, 4);
            final String name = string(n(tfTuple, 0));
            // TODO vars: List<String>
            // type: ITerm
            final IApplTerm latticeTuple = tuple(n(tfTuple, 3), 3);

            final Function lub = binaryFunction("Lub", n(latticeTuple, 0));
            final Function top = nullaryFunction(n(latticeTuple, 1));
            final Function bottom = nullaryFunction(n(latticeTuple, 2));
            final UserDefinedLattice udl = new UserDefinedLattice(top, bottom, lub);

            latticeDefs.__put(name, udl);
        }

        return ImmutableLatticeInfo.of(latticeDefs.freeze());
    }

    private Function nullaryFunction(ITerm term) {
        final FrameDescriptor frameDescriptor = new FrameDescriptor();

        frameStack.push(frameDescriptor);
        final ExpressionNode body = expressionNode(term);
        frameStack.pop();

        return new Function(null, frameDescriptor, new ArgToVarNode[0], body);
    }

    private Function binaryFunction(String op, ITerm term) {
        final IApplTerm appl = appl(term, op, 3);
        final String left = string(n(appl, 0));
        final String right = string(n(appl, 1));

        final FrameDescriptor frameDescriptor = new FrameDescriptor();
        final ArgToVarNode[] args = new ArgToVarNode[] { ArgToVarNode.of(frameDescriptor, 0, left),
            ArgToVarNode.of(frameDescriptor, 1, right) };

        frameStack.push(frameDescriptor);
        final ExpressionNode body = expressionNode(n(appl, 2));
        frameStack.pop();

        return new Function(null, frameDescriptor, args, body);
    }

    private ExpressionNode expressionNode(ITerm term) {
        switch(appl(term).getOp()) {
            case "Term": {
                final IApplTerm appl = appl(term, 2);
                final String consName = string(n(appl, 0));
                final ExpressionNode[] exprs = exprList(n(appl, 1));
                return new TermNode(consName, exprs);
            }
            case "Ref": {
                final IApplTerm appl = appl(term, 1);
                return readVarNode(n(appl, 0));
            }
            case "TopOf": {
                final IApplTerm appl = appl(term, 1);
                final String name = string(n(appl, 0));
                final LatticeItemRefNode latticeItemRefNode = new LatticeItemRefNode(LatticeItem.Top, name);
                initializable.add(latticeItemRefNode);
                return latticeItemRefNode;
            }
            case "BottomOf": {
                final IApplTerm appl = appl(term, 1);
                final String name = string(n(appl, 0));
                return new LatticeItemRefNode(LatticeItem.Bottom, name);
            }
            case "Prop": {
                final IApplTerm appl = appl(term, 2);
                final String propName = string(n(appl, 0));
                final ReadVarNode rhs = readVarNode(n(appl, 1));
                final PropNode propNode = PropNodeGen.create(propName, rhs);
                initializable.add(propNode);
                return propNode;
            }
            case "ExtProp": {
                final IApplTerm appl = appl(term, 2);
                final String propName = string(n(appl, 0));
                final ReadVarNode rhs = readVarNode(n(appl, 1));
                final ExtPropNode propNode = new ExtPropNode(propName, rhs);
                initializable.add(propNode);
                return propNode;
            }
            case "Tuple": {
                final IApplTerm appl = appl(term, 2);
                final List<ITerm> exprTerms = list(n(appl, 1));
                final ExpressionNode[] exprs = new ExpressionNode[exprTerms.size() + 1];
                exprs[0] = expressionNode(n(appl, 0));
                int i = 1;
                for(ITerm exprTerm : exprTerms) {
                    exprs[i] = expressionNode(exprTerm);
                    i++;
                }
                return new TupleNode(exprs);
            }
            case "Int": {
                final IApplTerm appl = appl(term, 1);
                final int value = Integer.parseInt(string(n(appl, 0)));
                return new IntLiteralNode(value);
            }
            case "String": {
                final IApplTerm appl = appl(term, 1);
                final String value = string(n(appl, 0));
                return new StringLiteralNode(value);
            }
            case "True": {
                appl(term, 0);
                return new BooleanLiteralNode(true);
            }
            case "False": {
                appl(term, 0);
                return new BooleanLiteralNode(false);
            }
            case "Type": {
                final IApplTerm appl = appl(term, 1);
                final ReadVarNode rvn = readVarNode(n(appl, 0));
                return new TypeNode(rvn);
            }
            case "Appl": {
                final IApplTerm appl = appl(term, 2);
                final FunRefNode reference = funRefNode(n(appl, 0));
                final ExpressionNode[] exprs = exprList(n(appl, 1));
                return new ApplicationNode(reference, exprs);
            }
            case "If": {
                final IApplTerm appl = appl(term, 3);
                return new IfNode(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)), expressionNode(n(appl, 2)));
            }
            case "Eq": {
                final IApplTerm appl = appl(term, 2);
                return EqualNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Neq": {
                final IApplTerm appl = appl(term, 2);
                return NotEqualNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "And": {
                final IApplTerm appl = appl(term, 2);
                return AndNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Or": {
                final IApplTerm appl = appl(term, 2);
                return OrNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Not": {
                final IApplTerm appl = appl(term, 1);
                return NotNodeGen.create(expressionNode(n(appl, 0)));
            }
            case "Lt": {
                final IApplTerm appl = appl(term, 2);
                return LtNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Lte": {
                final IApplTerm appl = appl(term, 2);
                return LteNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Gt": {
                final IApplTerm appl = appl(term, 2);
                return GtNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Gte": {
                final IApplTerm appl = appl(term, 2);
                return GteNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Add": {
                final IApplTerm appl = appl(term, 2);
                return AddNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Sub": {
                final IApplTerm appl = appl(term, 2);
                return SubNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Mul": {
                final IApplTerm appl = appl(term, 2);
                return MulNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Div": {
                final IApplTerm appl = appl(term, 2);
                return DivNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Mod": {
                final IApplTerm appl = appl(term, 2);
                return ModNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "Neg": {
                final IApplTerm appl = appl(term, 1);
                return NegNodeGen.create(expressionNode(n(appl, 0)));
            }
            case "Match": {
                final IApplTerm appl = appl(term, 2);
                final ExpressionNode expr = expressionNode(n(appl, 0));
                final List<ITerm> arms = list(n(appl, 1));
                final PatternNode[] patterns = new PatternNode[arms.size()];
                final ExpressionNode[] bodies = new ExpressionNode[arms.size()];
                int i = 0;
                for(ITerm arm : arms) {
                    final IApplTerm armAppl = appl(arm, "MatchArm", 2);
                    patterns[i] = patternNode(n(armAppl, 0));
                    bodies[i] = expressionNode(n(armAppl, 1));
                    i++;
                }
                return new MatchNode(expr, patterns, bodies);
            }
            case "SetLiteral": {
                final IApplTerm appl = appl(term, 1);
                final ExpressionNode[] exprs = exprList(n(appl, 0));
                return new SetLiteralNode(exprs);
            }
            case "MapLiteral": {
                final IApplTerm appl = appl(term, 1);
                final ExpressionNode[] exprs = exprList(n(appl, 0));
                return new MapLiteralNode(exprs);
            }
            case "SetComp": {
                final IApplTerm appl = appl(term, 4);
                final PatternNode[] patterns = patternList(n(appl, 1));
                final ExpressionNode expr = expressionNode(n(appl, 0));
                final ExpressionNode[] exprs = exprList(n(appl, 2));
                final CompPredicateNode[] preds = predList(n(appl, 3));
                return new SetCompNode(expr, patterns, exprs, preds);
            }
            case "MapComp": {
                final IApplTerm appl = appl(term, 4);
                final PatternNode[] patterns = patternList(n(appl, 1));
                final ExpressionNode expr = expressionNode(n(appl, 0));
                final ExpressionNode[] exprs = exprList(n(appl, 2));
                final CompPredicateNode[] preds = predList(n(appl, 3));
                return new MapCompNode(expr, patterns, exprs, preds);
            }
            case "MapLookup": {
                final IApplTerm appl = appl(term, 2);
                return new MapLookupNode(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "EmptySetOrMapLiteral": {
                appl(term, 0);
                return new EmptySetOrMapLiteral();
            }
            case "TermIndex": {
                final IApplTerm appl = appl(term, 1);
                final ReadVarNode var = readVarNode(n(appl, 0));
                return TermIndexNodeGen.create(var);
            }
            case "NaBL2Occurrence": {
                final IApplTerm appl = appl(term, 1);
                final IApplTerm occurrenceAppl = appl(n(appl, 0), "Occurrence", 3);
                final Namespace ns = namespace(n(occurrenceAppl, 0));
                final IApplTerm refAppl = appl(n(occurrenceAppl, 1), "Ref", 1);
                appl(n(occurrenceAppl, 2), "FSNoIndex", 0);
                final ReadVarNode rvn = readVarNode(n(refAppl, 0));
                return new NaBL2OccurrenceNode(ns, rvn);
            }
            case "SetUnion": {
                final IApplTerm appl = appl(term, 2);
                return SetUnionNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "SetMinus": {
                final IApplTerm appl = appl(term, 2);
                return SetMinusNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "SetContains": {
                final IApplTerm appl = appl(term, 2);
                return SetContainsNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
            case "SetIntersect": {
                final IApplTerm appl = appl(term, 2);
                return SetIntersectNodeGen.create(expressionNode(n(appl, 0)), expressionNode(n(appl, 1)));
            }
        }
        throw new AssertionError("Unrecognised ExpressionNode: " + term);
    }

    private Namespace namespace(ITerm n) {
        return Namespace.matcher().match(n).get();
    }

    private ReadVarNode readVarNode(final ITerm term) {
        final String name = string(term);
        return ReadVarNodeGen.create(Objects.requireNonNull(frameStack.peek().findFrameSlot(name)));
    }

    private PatternNode patternNode(ITerm term) {
        switch(appl(term).getOp()) {
            case "Term": {
                final IApplTerm appl = appl(term, 2);
                final String consName = string(n(appl, 0));
                final PatternNode[] childPatterns = patternList(n(appl, 1));
                switch(consName) {
                    case "Cons": {
                        if(childPatterns.length == 2) {
                            return new ConsPatternNode(childPatterns[0], childPatterns[1]);
                        } else {
                            break;
                        }
                    }
                    case "Nil": {
                        if(childPatterns.length == 0) {
                            return NilPatternNode.SINGLETON;
                        }
                    }
                }
                return new TermPatternNode(consName, childPatterns);
            }
            case "Tuple": {
                final IApplTerm appl = appl(term, 2);
                final List<ITerm> patternTerms = list(n(appl, 1));
                final PatternNode[] patterns = new PatternNode[patternTerms.size() + 1];
                patterns[0] = patternNode(n(appl, 0));
                int i = 1;
                for(ITerm exprTerm : patternTerms) {
                    patterns[i] = patternNode(exprTerm);
                    i++;
                }
                return new TuplePatternNode(patterns);
            }
            case "Wildcard": {
                appl(term, 0);
                return WildcardPatternNode.of();
            }
            case "Var": {
                return varPattern(term);
            }
            case "At": {
                final IApplTerm appl = appl(term, 2);
                return new AtPatternNode(varPattern(n(appl, 0)), patternNode(n(appl, 1)));
            }
            case "Int": {
                final IApplTerm appl = appl(term, 1);
                return new IntLiteralPatternNode(integer(n(appl, 0)));
            }
            case "String": {
                final IApplTerm appl = appl(term, 1);
                return new StringLiteralPatternNode(string(n(appl, 0)));
            }
        }
        throw new AssertionError("Unrecognised PatterNode: " + term);
    }

    private VarPatternNode varPattern(ITerm term) {
        final IApplTerm appl = appl(term, "Var", 1);
        final String name = string(n(appl, 0));
        return new VarPatternNode(frameStack.peek().findOrAddFrameSlot(name));
    }

    private FunRefNode funRefNode(ITerm term) {
        switch(appl(term).getOp()) {
            case "Ref": {
                final IApplTerm appl = appl(term, 1);
                final FunRefRefNode frrn = new FunRefRefNode(string(n(appl, 0)));
                initializable.add(frrn);
                return frrn;
            }
            case "LubOf": {
                final IApplTerm appl = appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Lub, string(n(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "GlbOf": {
                final IApplTerm appl = appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Glb, string(n(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "LeqOf": {
                final IApplTerm appl = appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Leq, string(n(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "GeqOf": {
                final IApplTerm appl = appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Geq, string(n(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "NLeqOf": {
                final IApplTerm appl = appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.NLeq, string(n(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "QualRef": {
                final IApplTerm appl = appl(term, 2);
                return new QualRefNode(string(n(appl, 0)), string(n(appl, 1)));
            }
        }
        throw new AssertionError("Unrecognised FunNode: " + term);
    }

    private CompPredicateNode compPredicateNode(ITerm term) {
        switch(appl(term).getOp()) {
            case "Predicate": {
                final IApplTerm appl = appl(term, 1);
                return new CompPredicateNode(expressionNode(n(appl, 0)));
            }
            case "MatchPredicate": {
                final IApplTerm appl = appl(term, 2);
                final ExpressionNode expr = expressionNode(n(appl, 0));
                final PatternNode[] patterns = patternList(n(appl, 1));
                return new SetCompMatchPredicateNode(expr, patterns);
            }
        }
        throw new AssertionError("Unrecognised CompPredicate: " + term);
    }

    private ArgToVarNode[] argList(final ITerm term) {
        final List<ITerm> argTerms = list(term);
        final ArgToVarNode[] args = new ArgToVarNode[argTerms.size()];
        int i = 0;
        for(ITerm argTerm : argTerms) {
            final String name = string(argTerm);
            final FrameDescriptor fd = frameStack.peek();
            args[i] = new ArgToVarNode(i, fd.findOrAddFrameSlot(name));
            i++;
        }
        return args;
    }

    private ExpressionNode[] exprList(final ITerm term) {
        final List<ITerm> exprTerms = list(term);
        final ExpressionNode[] exprs = new ExpressionNode[exprTerms.size()];
        int i = 0;
        for(ITerm exprTerm : exprTerms) {
            exprs[i] = expressionNode(exprTerm);
            i++;
        }
        return exprs;
    }

    private PatternNode[] patternList(final ITerm term) {
        final List<ITerm> patternTerms = list(term);
        final PatternNode[] patterns = new PatternNode[patternTerms.size()];
        int i = 0;
        for(ITerm patternTerm : patternTerms) {
            patterns[i] = patternNode(patternTerm);
            i++;
        }
        return patterns;
    }

    private CompPredicateNode[] predList(final ITerm term) {
        final List<ITerm> predTerms = list(term);
        final CompPredicateNode[] preds = new CompPredicateNode[predTerms.size()];
        int i = 0;
        for(ITerm predTerm : predTerms) {
            preds[i] = compPredicateNode(predTerm);
            i++;
        }
        return preds;
    }

    // ITerm helpers

    private static IApplTerm appl(ITerm term) {
        assertThat(term, instanceOf(IApplTerm.class));
        return (IApplTerm) term;
    }

    private static IApplTerm appl(ITerm term, int arity) {
        assertThat(term, instanceOf(IApplTerm.class));
        final IApplTerm appl = (IApplTerm) term;
        assertEquals(appl.getArity(), arity);
        return appl;
    }

    private static IApplTerm appl(ITerm term, String op, int arity) {
        assertThat(term, instanceOf(IApplTerm.class));
        final IApplTerm appl = (IApplTerm) term;
        assertEquals(appl.getOp(), op);
        assertEquals(appl.getArity(), arity);
        return appl;
    }

    private static IApplTerm tuple(ITerm term, int arity) {
        return appl(term, "", arity);
    }

    private static List<ITerm> list(ITerm term) {
        assertThat(term, instanceOf(IListTerm.class));
        final IListTerm listTerm = (IListTerm) term;
        final ArrayList<ITerm> list = new ArrayList<>();
        for(ITerm elem : ListTerms.iterable(listTerm)) {
            list.add(elem);
        }
        return list;
    }

    private static String string(ITerm term) {
        assertThat(term, instanceOf(IStringTerm.class));
        return ((IStringTerm) term).getValue();
    }

    private static int integer(ITerm term) {
        assertThat(term, instanceOf(IIntTerm.class));
        return ((IIntTerm) term).getValue();
    }

    private static ITerm n(IApplTerm term, int n) {
        return term.getArgs().get(n);
    }
}
