package mb.flowspec.runtime.interpreter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.metaborg.util.Ref;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.oracle.truffle.api.frame.FrameDescriptor;

import io.usethesource.capsule.BinaryRelation;
import mb.flowspec.controlflow.ICFGNode;
import mb.flowspec.controlflow.IFlowSpecSolution;
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
import mb.flowspec.runtime.solver.ImmutableMapType;
import mb.flowspec.runtime.solver.ImmutableSetType;
import mb.flowspec.runtime.solver.ImmutableSimpleType;
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
import mb.flowspec.terms.B;
import mb.flowspec.terms.M;
import mb.nabl2.scopegraph.terms.ImmutableNamespace;
import mb.nabl2.scopegraph.terms.Namespace;
import mb.nabl2.stratego.StrategoTermIndices;
import mb.nabl2.stratego.TermIndex;
import mb.nabl2.util.ImmutableTuple2;
import mb.nabl2.util.Tuple2;

public class InterpreterBuilder {
    protected final ArrayList<Initializable> initializable = new ArrayList<>();
    protected StaticInfo staticInfo = new StaticInfo();
    protected final Deque<FrameDescriptor> frameStack = new ArrayDeque<>();
    protected String moduleName;

    // Public API

    public InterpreterBuilder add(IStrategoTerm term, String moduleName) {
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
     * Pass the solution to the interpreter AST so it can save references to the CFG and the resolution result in
     * certain places
     * @param termFactory 
     */
    public StaticInfo build(ITermFactory termFactory, IFlowSpecSolution solution,
        Map<String, Map<ICFGNode, Ref<IStrategoTerm>>> preProperties) {
        InitValues initValues = new InitValues(solution.config(), solution.controlFlowGraph(), preProperties,
            solution.scopeGraph(), solution.nameResolutionCache(), solution.unifier(), solution.astProperties(), staticInfo.functions.functions,
            staticInfo.lattices.latticeDefs, new B(termFactory));

        for(Initializable i : initializable) {
            i.init(initValues);
        }
        return staticInfo;
    }

    // Matching functions

    @SuppressWarnings({ "rawtypes", "unchecked" }) private StaticInfo staticInfo(IStrategoTerm term) {
        final IStrategoTuple appl = M.tuple(term, 3);
        final LatticeInfo latticeInfo = latticeInfo(M.at(appl, 1));
        final FunctionInfo functionInfo = functionInfo(M.at(appl, 2));
        final Map<String, CompleteLattice> latticeDefs = latticeInfo.latticeDefs;

        final Map<String, Metadata<?>> propMetadata = new HashMap<>();
        final BinaryRelation.Transient<String, String> dependsOn = BinaryRelation.Transient.of();

        for(IStrategoTerm tfInfoTerm : M.list(M.at(appl, 0))) {
            final IStrategoTuple tfInfoTuple = M.tuple(tfInfoTerm, 2);
            final String propName = M.string(M.at(tfInfoTuple, 0));
            final IStrategoTuple tfNestedTuple = M.tuple(M.at(tfInfoTuple, 1), 3);
            final Type type = type(M.at(tfNestedTuple, 0));
            final Direction dir = direction(M.at(tfNestedTuple, 1));
            final IStrategoList funs = M.list(M.at(tfNestedTuple, 2));

            final Map<Tuple2<String, Integer>, TransferFunction> tfMap = new HashMap<>();
            for(IStrategoTerm funTerm : funs) {
                final IStrategoTuple funTuple = M.tuple(funTerm, 2);
                int index = M.integer(M.at(funTuple, 0));
                final TransferFunction tf = transferFunction(M.at(funTuple, 1));
                tfMap.put(ImmutableTuple2.of(moduleName, index), tf);
            }

            propMetadata.put(propName, new Metadata(dir, latticeFromType(latticeDefs, type), Collections.unmodifiableMap(tfMap)));
        }

        return new StaticInfo(dependsOn.freeze(), Collections.unmodifiableMap(propMetadata), functionInfo, latticeInfo);
    }

    public static Type type(IStrategoTerm term) {
        switch(M.appl(term).getName()) {
            case "Tuple": {
                IStrategoAppl appl = M.appl(term, 2);
                return ImmutableTupleType.of(type(M.at(appl, 0)), type(M.at(appl, 1)));
            }
            case "Map": {
                IStrategoAppl appl = M.appl(term, 2);
                return ImmutableMapType.of(type(M.at(appl, 0)), type(M.at(appl, 1)));
            }
            case "Set": {
                IStrategoAppl appl = M.appl(term, 1);
                return ImmutableSetType.of(type(M.at(appl, 0)));
            }
            case "UserType": {
                IStrategoAppl appl = M.appl(term, 2);
                IStrategoList typeTerms = M.list(M.at(appl, 1));
                Type[] types = new Type[typeTerms.size()];
                int i = 0;
                for(IStrategoTerm typeTerm : typeTerms) {
                    types[i] = type(typeTerm);
                    i++;
                }
                return ImmutableUserType.of(M.string(M.at(appl, 0)), types);
            }
            default: {
                IStrategoAppl appl = M.appl(term, 0);
                return ImmutableSimpleType.of(SimpleType.SimpleTypeEnum.valueOf(appl.getName()));
            }
        }
    }

    public static Direction direction(IStrategoTerm term) {
        IStrategoAppl appl = M.appl(term, 0);
        switch(appl.getName()) {
            case "Bw":
                return Direction.Backward;
            case "Fw":
                return Direction.Forward;
        }
        throw new AssertionError("Direction was not equal to Fw or Bw");
    }

    private TransferFunction transferFunction(IStrategoTerm term) {
        final IStrategoAppl appl = M.appl(term, 2);
        final ArgToVarNode[] args = argList(M.at(appl, 0));
        final Where body = where(M.at(appl, 1));
        switch(appl.getName()) {
            case "TransferFunction":
                return new TransferFunction(null, frameStack.peek(), args, body);
            case "InitFunction":
                return new InitFunction(null, frameStack.peek(), args, body);
        }
        throw new AssertionError("Transfer function was not equal to TransferFunction or InitFunction");
    }

    private Where where(IStrategoTerm appl) {
        final IStrategoAppl whereAppl = M.appl(appl, "Where", 2);
        final IStrategoList varList = M.list(M.at(whereAppl, 1));
        final WriteVarNode[] writeVars = new WriteVarNode[varList.size()];

        int i = 0;
        for(IStrategoTerm varTerm : varList) {
            final IStrategoAppl varAppl = M.appl(varTerm, "Binding", 2);
            final String name = M.string(M.at(varAppl, 0));
            final ExpressionNode expr = expressionNode(M.at(varAppl, 1));
            final FrameDescriptor fd = frameStack.peek();
            writeVars[i] = WriteVarNodeGen.create(expr, fd.findOrAddFrameSlot(name));
            i++;
        }

        final ExpressionNode expr = expressionNode(M.at(whereAppl, 0));
        final Where body = new Where(writeVars, expr);
        return body;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) public static CompleteLattice
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

    private FunctionInfo functionInfo(IStrategoTerm term) {
        Map<String, Function> map = new HashMap<>();

        for(IStrategoTerm functionTerm : M.list(term)) {
            final IStrategoAppl appl = M.appl(functionTerm, "FunDef", 3);
            final String name = M.string(M.at(appl, 0));
            final IStrategoList argsList = M.list(M.at(appl, 1));
            final IStrategoTerm bodyTerm = M.at(appl, 2);

            final FrameDescriptor frameDescriptor = new FrameDescriptor();
            final ArgToVarNode[] args = new ArgToVarNode[argsList.size()];
            int i = 0;
            for(IStrategoTerm argTerm : argsList) {
                final IStrategoAppl arg = M.appl(argTerm, "Arg", 2);
                args[i] = ArgToVarNode.of(frameDescriptor, i, M.string(M.at(arg, 0)));
                // M.at(arg, 1): Type
                i++;
            }

            frameStack.push(frameDescriptor);
            final ExpressionNode body = expressionNode(bodyTerm);
            frameStack.pop();

            final Function function = new Function(null, frameDescriptor, args, body);

            map.put(name, function);
        }

        return new FunctionInfo(Collections.unmodifiableMap(map));
    }

    @SuppressWarnings("rawtypes") private LatticeInfo latticeInfo(IStrategoTerm term) {
        final Map<String, CompleteLattice> latticeDefs = new HashMap<>();
        latticeDefs.put("MaySet", new FullSetLattice<>());
        latticeDefs.put("MustSet", new FullSetLattice<>().flip());

        for(IStrategoTerm tfTerm : M.list(term)) {
            final IStrategoTuple tfTuple = M.tuple(tfTerm, 4);
            final String name = M.string(M.at(tfTuple, 0));
            // TODO vars: List<String>
            // type: IStrategoTerm
            final IStrategoTuple latticeTuple = M.tuple(M.at(tfTuple, 3), 3);

            final Function lub = binaryFunction("Lub", M.at(latticeTuple, 0));
            final Function top = nullaryFunction(M.at(latticeTuple, 1));
            final Function bottom = nullaryFunction(M.at(latticeTuple, 2));
            final UserDefinedLattice udl = new UserDefinedLattice(top, bottom, lub);

            latticeDefs.put(name, udl);
        }

        return new LatticeInfo(Collections.unmodifiableMap(latticeDefs));
    }

    private Function nullaryFunction(IStrategoTerm term) {
        final FrameDescriptor frameDescriptor = new FrameDescriptor();

        frameStack.push(frameDescriptor);
        final ExpressionNode body = expressionNode(term);
        frameStack.pop();

        return new Function(null, frameDescriptor, new ArgToVarNode[0], body);
    }

    private Function binaryFunction(String op, IStrategoTerm term) {
        final IStrategoAppl appl = M.appl(term, op, 3);
        final String left = M.string(M.at(appl, 0));
        final String right = M.string(M.at(appl, 1));

        final FrameDescriptor frameDescriptor = new FrameDescriptor();
        final ArgToVarNode[] args = new ArgToVarNode[] { ArgToVarNode.of(frameDescriptor, 0, left),
            ArgToVarNode.of(frameDescriptor, 1, right) };

        frameStack.push(frameDescriptor);
        final ExpressionNode body = expressionNode(M.at(appl, 2));
        frameStack.pop();

        return new Function(null, frameDescriptor, args, body);
    }

    private ExpressionNode expressionNode(IStrategoTerm term) {
        switch(M.appl(term).getName()) {
            case "Term": {
                final IStrategoAppl appl = M.appl(term, 2);
                final String consName = M.string(M.at(appl, 0));
                final ExpressionNode[] exprs = exprList(M.at(appl, 1));
                final TermNode termNode = new TermNode(consName, exprs);
                initializable.add(termNode);
                return termNode;
            }
            case "Ref": {
                final IStrategoAppl appl = M.appl(term, 1);
                return readVarNode(M.at(appl, 0));
            }
            case "TopOf": {
                final IStrategoAppl appl = M.appl(term, 1);
                final String name = M.string(M.at(appl, 0));
                final LatticeItemRefNode latticeItemRefNode = new LatticeItemRefNode(LatticeItem.Top, name);
                initializable.add(latticeItemRefNode);
                return latticeItemRefNode;
            }
            case "BottomOf": {
                final IStrategoAppl appl = M.appl(term, 1);
                final String name = M.string(M.at(appl, 0));
                final LatticeItemRefNode latticeItemRefNode = new LatticeItemRefNode(LatticeItem.Bottom, name);
                initializable.add(latticeItemRefNode);
                return latticeItemRefNode;
            }
            case "Prop": {
                final IStrategoAppl appl = M.appl(term, 2);
                final String propName = M.string(M.at(appl, 0));
                final ReadVarNode rhs = readVarNode(M.at(appl, 1));
                final PropNode propNode = PropNodeGen.create(propName, rhs);
                initializable.add(propNode);
                return propNode;
            }
            case "ExtProp": {
                final IStrategoAppl appl = M.appl(term, 2);
                final String propName = M.string(M.at(appl, 0));
                final ReadVarNode rhs = readVarNode(M.at(appl, 1));
                final ExtPropNode propNode = new ExtPropNode(propName, rhs);
                initializable.add(propNode);
                return propNode;
            }
            case "Tuple": {
                final IStrategoAppl appl = M.appl(term, 2);
                final IStrategoList exprTerms = M.list(M.at(appl, 1));
                final ExpressionNode[] exprs = new ExpressionNode[exprTerms.size() + 1];
                exprs[0] = expressionNode(M.at(appl, 0));
                int i = 1;
                for(IStrategoTerm exprTerm : exprTerms) {
                    exprs[i] = expressionNode(exprTerm);
                    i++;
                }
                return new TupleNode(exprs);
            }
            case "Int": {
                final IStrategoAppl appl = M.appl(term, 1);
                final int value = Integer.parseInt(M.string(M.at(appl, 0)));
                return new IntLiteralNode(value);
            }
            case "String": {
                final IStrategoAppl appl = M.appl(term, 1);
                final String value = M.string(M.at(appl, 0));
                return new StringLiteralNode(value);
            }
            case "True": {
                M.appl(term, 0);
                final BooleanLiteralNode booleanLiteralNode = new BooleanLiteralNode(true);
                initializable.add(booleanLiteralNode);
                return booleanLiteralNode;
            }
            case "False": {
                M.appl(term, 0);
                final BooleanLiteralNode booleanLiteralNode = new BooleanLiteralNode(false);
                initializable.add(booleanLiteralNode);
                return booleanLiteralNode;
            }
            case "Type": {
                final IStrategoAppl appl = M.appl(term, 1);
                final ReadVarNode rvn = readVarNode(M.at(appl, 0));
                return new TypeNode(rvn);
            }
            case "Appl": {
                final IStrategoAppl appl = M.appl(term, 2);
                final FunRefNode reference = funRefNode(M.at(appl, 0));
                final ExpressionNode[] exprs = exprList(M.at(appl, 1));
                return new ApplicationNode(reference, exprs);
            }
            case "If": {
                final IStrategoAppl appl = M.appl(term, 3);
                return new IfNode(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)),
                    expressionNode(M.at(appl, 2)));
            }
            case "Eq": {
                final IStrategoAppl appl = M.appl(term, 2);
                return EqualNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "NEq": {
                final IStrategoAppl appl = M.appl(term, 2);
                return NotNodeGen.create(EqualNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1))));
            }
            case "And": {
                final IStrategoAppl appl = M.appl(term, 2);
                return AndNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Or": {
                final IStrategoAppl appl = M.appl(term, 2);
                return OrNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Not": {
                final IStrategoAppl appl = M.appl(term, 1);
                return NotNodeGen.create(expressionNode(M.at(appl, 0)));
            }
            case "Lt": {
                final IStrategoAppl appl = M.appl(term, 2);
                return LtNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Lte": {
                final IStrategoAppl appl = M.appl(term, 2);
                return LteNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Gt": {
                final IStrategoAppl appl = M.appl(term, 2);
                return GtNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Gte": {
                final IStrategoAppl appl = M.appl(term, 2);
                return GteNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Add": {
                final IStrategoAppl appl = M.appl(term, 2);
                return AddNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Sub": {
                final IStrategoAppl appl = M.appl(term, 2);
                return SubNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Mul": {
                final IStrategoAppl appl = M.appl(term, 2);
                return MulNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Div": {
                final IStrategoAppl appl = M.appl(term, 2);
                return DivNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Mod": {
                final IStrategoAppl appl = M.appl(term, 2);
                return ModNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "Neg": {
                final IStrategoAppl appl = M.appl(term, 1);
                return NegNodeGen.create(expressionNode(M.at(appl, 0)));
            }
            case "Match": {
                final IStrategoAppl appl = M.appl(term, 2);
                final ExpressionNode expr = expressionNode(M.at(appl, 0));
                final IStrategoList arms = M.list(M.at(appl, 1));
                final PatternNode[] patterns = new PatternNode[arms.size()];
                final ExpressionNode[] bodies = new ExpressionNode[arms.size()];
                int i = 0;
                for(IStrategoTerm arm : arms) {
                    final IStrategoAppl armAppl = M.appl(arm, "MatchArm", 2);
                    patterns[i] = patternNode(M.at(armAppl, 0));
                    bodies[i] = expressionNode(M.at(armAppl, 1));
                    i++;
                }
                return new MatchNode(expr, patterns, bodies);
            }
            case "SetLiteral": {
                final IStrategoAppl appl = M.appl(term, 1);
                final ExpressionNode[] exprs = exprList(M.at(appl, 0));
                return new SetLiteralNode(exprs);
            }
            case "MapLiteral": {
                final IStrategoAppl appl = M.appl(term, 1);
                final ExpressionNode[] exprs = exprList(M.at(appl, 0));
                return new MapLiteralNode(exprs);
            }
            case "SetComp": {
                final IStrategoAppl appl = M.appl(term, 4);
                final PatternNode[] patterns = patternList(M.at(appl, 1));
                final ExpressionNode expr = expressionNode(M.at(appl, 0));
                final ExpressionNode[] exprs = exprList(M.at(appl, 2));
                final CompPredicateNode[] preds = predList(M.at(appl, 3));
                return new SetCompNode(expr, patterns, exprs, preds);
            }
            case "MapComp": {
                final IStrategoAppl appl = M.appl(term, 4);
                final PatternNode[] patterns = patternList(M.at(appl, 1));
                final ExpressionNode expr = expressionNode(M.at(appl, 0));
                final ExpressionNode[] exprs = exprList(M.at(appl, 2));
                final CompPredicateNode[] preds = predList(M.at(appl, 3));
                return new MapCompNode(expr, patterns, exprs, preds);
            }
            case "MapLookup": {
                final IStrategoAppl appl = M.appl(term, 2);
                return new MapLookupNode(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "EmptySetOrMapLiteral": {
                M.appl(term, 0);
                return new EmptySetOrMapLiteral();
            }
            case "TermIndex": {
                final IStrategoAppl appl = M.appl(term, 1);
                final ReadVarNode var = readVarNode(M.at(appl, 0));
                return TermIndexNodeGen.create(var);
            }
            case "NaBL2Occurrence": {
                final IStrategoAppl appl = M.appl(term, 1);
                final IStrategoAppl occurrenceAppl = M.appl(M.at(appl, 0), "Occurrence", 3);
                final Namespace ns = namespace(M.at(occurrenceAppl, 0));
                final IStrategoAppl refAppl = M.appl(M.at(occurrenceAppl, 1), "Ref", 1);
                M.appl(M.at(occurrenceAppl, 2), "FSNoIndex", 0);
                final ReadVarNode rvn = readVarNode(M.at(refAppl, 0));
                return new NaBL2OccurrenceNode(ns, rvn);
            }
            case "SetUnion": {
                final IStrategoAppl appl = M.appl(term, 2);
                return SetUnionNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "SetDifference": {
                final IStrategoAppl appl = M.appl(term, 2);
                return SetMinusNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "SetContains": {
                final IStrategoAppl appl = M.appl(term, 2);
                return SetContainsNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
            case "SetIntersection": {
                final IStrategoAppl appl = M.appl(term, 2);
                return SetIntersectNodeGen.create(expressionNode(M.at(appl, 0)), expressionNode(M.at(appl, 1)));
            }
        }
        throw new AssertionError("Unrecognised ExpressionNode: " + term);
    }

    public static Namespace namespace(IStrategoTerm term) {
        final String ns;
        switch(M.appl(term).getName()) {
            case "DefaultNameSpace": {
                M.appl(term, 0);
                ns = "";
                break;
            }
            case "NameSpace": {
                IStrategoAppl appl = M.appl(term, 1);
                ns = M.string(M.at(appl, 0));
                break;
            }
            default: {
                throw new AssertionError("Unrecognised Namespace: " + term);
            }
        }
        Optional<TermIndex> optTI = StrategoTermIndices.get(term);
        if(optTI.isPresent()) {
            return ImmutableNamespace.of(ns)
                .withAttachments(ImmutableClassToInstanceMap.of(TermIndex.class, optTI.get()));
        } else {
            return ImmutableNamespace.of(ns);
        }
    }

    private ReadVarNode readVarNode(final IStrategoTerm term) {
        final String name = M.string(term);
        return ReadVarNodeGen.create(Objects.requireNonNull(frameStack.peek().findFrameSlot(name)));
    }

    private PatternNode patternNode(IStrategoTerm term) {
        switch(M.appl(term).getName()) {
            case "Term": {
                final IStrategoAppl appl = M.appl(term, 2);
                final String consName = M.string(M.at(appl, 0));
                final PatternNode[] childPatterns = patternList(M.at(appl, 1));
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
                final IStrategoAppl appl = M.appl(term, 2);
                final IStrategoList patternTerms = M.list(M.at(appl, 1));
                final PatternNode[] patterns = new PatternNode[patternTerms.size() + 1];
                patterns[0] = patternNode(M.at(appl, 0));
                int i = 1;
                for(IStrategoTerm exprTerm : patternTerms) {
                    patterns[i] = patternNode(exprTerm);
                    i++;
                }
                return new TuplePatternNode(patterns);
            }
            case "Wildcard": {
                M.appl(term, 0);
                return WildcardPatternNode.of();
            }
            case "Var": {
                return varPattern(term);
            }
            case "At": {
                final IStrategoAppl appl = M.appl(term, 2);
                return new AtPatternNode(varPattern(M.at(appl, 0)), patternNode(M.at(appl, 1)));
            }
            case "Int": {
                final IStrategoAppl appl = M.appl(term, 1);
                return new IntLiteralPatternNode(M.integer(M.at(appl, 0)));
            }
            case "String": {
                final IStrategoAppl appl = M.appl(term, 1);
                return new StringLiteralPatternNode(M.string(M.at(appl, 0)));
            }
        }
        throw new AssertionError("Unrecognised PatterNode: " + term);
    }

    private VarPatternNode varPattern(IStrategoTerm term) {
        final IStrategoAppl appl = M.appl(term, "Var", 1);
        final String name = M.string(M.at(appl, 0));
        return new VarPatternNode(frameStack.peek().findOrAddFrameSlot(name));
    }

    private FunRefNode funRefNode(IStrategoTerm term) {
        switch(M.appl(term).getName()) {
            case "Ref": {
                final IStrategoAppl appl = M.appl(term, 1);
                final FunRefRefNode frrn = new FunRefRefNode(M.string(M.at(appl, 0)));
                initializable.add(frrn);
                return frrn;
            }
            case "LubOf": {
                final IStrategoAppl appl = M.appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Lub, M.string(M.at(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "GlbOf": {
                final IStrategoAppl appl = M.appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Glb, M.string(M.at(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "LeqOf": {
                final IStrategoAppl appl = M.appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Leq, M.string(M.at(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "GeqOf": {
                final IStrategoAppl appl = M.appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.Geq, M.string(M.at(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "NLeqOf": {
                final IStrategoAppl appl = M.appl(term, 1);
                final LatticeOpRefNode lorn = new LatticeOpRefNode(LatticeOp.NLeq, M.string(M.at(appl, 0)));
                initializable.add(lorn);
                return lorn;
            }
            case "QualRef": {
                final IStrategoAppl appl = M.appl(term, 2);
                return new QualRefNode(M.string(M.at(appl, 0)), M.string(M.at(appl, 1)));
            }
        }
        throw new AssertionError("Unrecognised FunNode: " + term);
    }

    private CompPredicateNode compPredicateNode(IStrategoTerm term) {
        switch(M.appl(term).getName()) {
            case "Predicate": {
                final IStrategoAppl appl = M.appl(term, 1);
                return new CompPredicateNode(expressionNode(M.at(appl, 0)));
            }
            case "MatchPredicate": {
                final IStrategoAppl appl = M.appl(term, 2);
                final ExpressionNode expr = expressionNode(M.at(appl, 0));
                final PatternNode[] patterns = patternList(M.at(appl, 1));
                return new SetCompMatchPredicateNode(expr, patterns);
            }
        }
        throw new AssertionError("Unrecognised CompPredicate: " + term);
    }

    private ArgToVarNode[] argList(final IStrategoTerm term) {
        final IStrategoList argTerms = M.list(term);
        final ArgToVarNode[] args = new ArgToVarNode[argTerms.size()];
        int i = 0;
        for(IStrategoTerm argTerm : argTerms) {
            final String name = M.string(argTerm);
            final FrameDescriptor fd = frameStack.peek();
            args[i] = new ArgToVarNode(i, fd.findOrAddFrameSlot(name));
            i++;
        }
        return args;
    }

    private ExpressionNode[] exprList(final IStrategoTerm term) {
        final IStrategoList exprTerms = M.list(term);
        final ExpressionNode[] exprs = new ExpressionNode[exprTerms.size()];
        int i = 0;
        for(IStrategoTerm exprTerm : exprTerms) {
            exprs[i] = expressionNode(exprTerm);
            i++;
        }
        return exprs;
    }

    private PatternNode[] patternList(final IStrategoTerm term) {
        final IStrategoList patternTerms = M.list(term);
        final PatternNode[] patterns = new PatternNode[patternTerms.size()];
        int i = 0;
        for(IStrategoTerm patternTerm : patternTerms) {
            patterns[i] = patternNode(patternTerm);
            i++;
        }
        return patterns;
    }

    private CompPredicateNode[] predList(final IStrategoTerm term) {
        final IStrategoList predTerms = M.list(term);
        final CompPredicateNode[] preds = new CompPredicateNode[predTerms.size()];
        int i = 0;
        for(IStrategoTerm predTerm : predTerms) {
            preds[i] = compPredicateNode(predTerm);
            i++;
        }
        return preds;
    }
}
