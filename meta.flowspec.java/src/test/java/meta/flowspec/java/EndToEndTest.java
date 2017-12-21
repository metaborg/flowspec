package meta.flowspec.java;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.metaborg.meta.nabl2.controlflow.terms.ControlFlowGraph;
import org.metaborg.meta.nabl2.controlflow.terms.ICFGNode;
import org.metaborg.meta.nabl2.controlflow.terms.TransferFunctionAppl;
import org.metaborg.meta.nabl2.stratego.ImmutableTermIndex;
import org.metaborg.meta.nabl2.stratego.TermIndex;
import org.metaborg.meta.nabl2.terms.IApplTerm;
import org.metaborg.meta.nabl2.terms.IIntTerm;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.ITermVar;
import org.metaborg.meta.nabl2.terms.generic.TB;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.java.interpreter.Where;
import meta.flowspec.java.interpreter.expressions.ExpressionNode;
import meta.flowspec.java.interpreter.expressions.IntLiteralNode;
import meta.flowspec.java.interpreter.expressions.ReadPropNode;
import meta.flowspec.java.interpreter.expressions.SetLiteralNode;
import meta.flowspec.java.interpreter.locals.ArgToVarNode;
import meta.flowspec.java.interpreter.locals.ReadVarNodeGen;
import meta.flowspec.java.interpreter.locals.WriteVarNode;
import meta.flowspec.java.lattice.CompleteLattice;
import meta.flowspec.java.lattice.FullSetLattice;
import meta.flowspec.java.solver.ImmutableMetadata;
import meta.flowspec.java.solver.MaximalFixedPoint;
import meta.flowspec.java.solver.Metadata;
import meta.flowspec.java.solver.Metadata.Direction;
import meta.flowspec.java.solver.Type;

public class EndToEndTest {
    @Test
    public void emptyCFG() {
        ControlFlowGraph<ICFGNode> cfg = new ControlFlowGraph<>();
        MaximalFixedPoint.<ICFGNode>solve(cfg, Map.Immutable.of(), BinaryRelation.Immutable.of(), Map.Immutable.of());
        assertEquals(cfg, new ControlFlowGraph<>());
    }

    @Ignore("broken because of ITerm requirement in lattices to easily print")
    @Test
    public void propagatePropertySingleStep() {
        ControlFlowGraph<ICFGNode> cfg = new ControlFlowGraph<>();

        String propertyName = "C";

        CFGNode start = new CFGNode("R", "start");
        CFGNode nodeA = new CFGNode("AR", "a");
        CFGNode nodeB = new CFGNode("BR", "b");
        CFGNode end   = new CFGNode("R", "end");

        FrameDescriptor fdTransA = new FrameDescriptor();
        String successorNodeName = "succ";
        TransferFunction transA =
            new TransferFunction(
                fdTransA,
                new ArgToVarNode[] {
                    new ArgToVarNode(0, fdTransA.addFrameSlot(successorNodeName, FrameSlotKind.Object))
                },
                new Where(
                    new WriteVarNode[] {},
                    new ReadPropNode(
                        cfg,
                        propertyName,
                        ReadVarNodeGen.create(fdTransA.findFrameSlot(successorNodeName)))));

        FrameDescriptor fdTransB = new FrameDescriptor();
        TransferFunction transB = 
            new TransferFunction(
                fdTransB,
                new ArgToVarNode[] {
                    new ArgToVarNode(0, fdTransB.addFrameSlot(successorNodeName, FrameSlotKind.Object))
                },
                new Where(
                    new WriteVarNode[] {},
                    new SetLiteralNode(new ExpressionNode[] { new IntLiteralNode(42) })));

        cfg.addDirectEdge(start, nodeA);
        cfg.addDirectEdge(nodeA, nodeB);
        cfg.addDirectEdge(nodeB, end);

        cfg.addTFAppl(nodeA, propertyName, new TransferFunctionAppl(0, new Object[] {}));
        cfg.addTFAppl(nodeB, propertyName, new TransferFunctionAppl(1, new Object[] {}));

        Map<String, Metadata> propMetadata = Map.Immutable.of(propertyName, ImmutableMetadata.of(Direction.Backward, (CompleteLattice) new FullSetLattice<IIntTerm>(), new Type()));

        BinaryRelation.Immutable<String, String> propDependsOn = BinaryRelation.Immutable.of();

        Map<String, TransferFunction[]> transferFuns = Map.Immutable.of(propertyName, new TransferFunction[] {transA, transB});

        MaximalFixedPoint.<ICFGNode>solve(cfg, propMetadata, propDependsOn, transferFuns);

        assertEquals(Set.Immutable.of(42), cfg.getProperty(nodeB, propertyName));
        assertEquals(Set.Immutable.of(42), cfg.getProperty(nodeA, propertyName));
    }

    @Ignore("incomplete")
    @Test
    public void reachingDefinitionsExample1() {
        ControlFlowGraph<ICFGNode> cfg = new ControlFlowGraph<>();

        String propertyName = "C";

        CFGNode start = (CFGNode) new CFGNode("", "start").withAttachments(ImmutableClassToInstanceMap.builder().put(TermIndex.class, ImmutableTermIndex.of("", 0)).build());
        CFGNode node1 = new CFGNode("", "y := x");
        CFGNode node2 = new CFGNode("", "z := 1");
        CFGNode node3 = new CFGNode("", "y > 1");
        CFGNode node4 = new CFGNode("", "z := z * y");
        CFGNode node5 = new CFGNode("", "y := y - 1");
        CFGNode node6 = new CFGNode("", "y := 0");
        CFGNode end   = new CFGNode("", "end");

        String successorNodeName = "succ";

        FrameDescriptor fdTrans1 = new FrameDescriptor();
        Optional<Integer> unknown = Optional.empty();
        ExpressionNode[] startSet = new ExpressionNode[] {
            // TODO add expressions that can evaluate to tuples and optionals
//        startSet = startSet.__insert(ImmutableTuple2.of("x", unknown));
//        startSet = startSet.__insert(ImmutableTuple2.of("y", unknown));
//        startSet = startSet.__insert(ImmutableTuple2.of("z", unknown));
        };
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        TransferFunction trans1 = 
            new TransferFunction(
                fdTrans1, 
                new ArgToVarNode[] {
                    new ArgToVarNode(0, fdTrans1.addFrameSlot(successorNodeName, FrameSlotKind.Object))
                }, 
                new Where(
                    new WriteVarNode[] {}, 
                    new SetLiteralNode(startSet)));

        FrameDescriptor fdTrans2 = new FrameDescriptor();
        TransferFunction trans2 = 
            new TransferFunction(
                fdTrans2, 
                new ArgToVarNode[] {
                    new ArgToVarNode(0, fdTrans2.addFrameSlot(successorNodeName, FrameSlotKind.Object))
                },  
                new Where(
                    new WriteVarNode[] {}, 
                    new ReadPropNode(
                        cfg, 
                        propertyName, 
                        ReadVarNodeGen.create(fdTrans2.findFrameSlot(successorNodeName)))));

        // We really need functions here, maybe even pattern matching
//        FrameDescriptor fdTrans3 = new FrameDescriptor();
//        TransferFunction trans3 = 
//            new TransferFunction(
//                fdTrans3, 
//                new ArgToVarNode[] {
//                    new ArgToVarNode(0, fdTrans3.addFrameSlot(successorNodeName, FrameSlotKind.Object))
//                },  
//                new Where(
//                    new WriteVarNode[] {}, 
//                    SetUnionNodeGen.create(
//                        SetFilterNodeGen.create(
//                            new ReadPropNode(
//                                cfg, 
//                                propertyName, 
//                                ReadVarNodeGen.create(fdTrans3.findFrameSlot(successorNodeName))),
//                            new AbsNode()),
//                        new SetLiteralNode(HashTreePSet.singleton(ImmutableTuple2.of("y", Optional.of(1)))))));

        cfg.addDirectEdge(start, node1);
        cfg.addDirectEdge(node1, node2);
        cfg.addDirectEdge(node2, node3);
        cfg.addDirectEdge(node3, node4);
        cfg.addDirectEdge(node4, node5);
        cfg.addDirectEdge(node5, node6);
        cfg.addDirectEdge(node6, end);
        cfg.addDirectEdge(node5, node3);

        cfg.addTFAppl(node1, propertyName, new TransferFunctionAppl(0, new Object[] {}));
        cfg.addTFAppl(node2, propertyName, new TransferFunctionAppl(1, new Object[] {}));
        cfg.addTFAppl(node3, propertyName, new TransferFunctionAppl(1, new Object[] {}));
        cfg.addTFAppl(node4, propertyName, new TransferFunctionAppl(1, new Object[] {}));
        cfg.addTFAppl(node5, propertyName, new TransferFunctionAppl(1, new Object[] {}));
        cfg.addTFAppl(node6, propertyName, new TransferFunctionAppl(1, new Object[] {}));

        // IApplTerm to describe Optional<Integer>
        Map<String, Metadata> propMetadata = Map.Immutable.of(propertyName, ImmutableMetadata.of(Direction.Forward, (CompleteLattice) new FullSetLattice<IApplTerm>(), new Type()));

        BinaryRelation.Immutable<String, String> propDependsOn = BinaryRelation.Immutable.of();

        Map<String, TransferFunction[]> transferFuns = Map.Immutable.of(propertyName, new TransferFunction[] {trans1, trans2});

        MaximalFixedPoint.<ICFGNode>solve(cfg, propMetadata, propDependsOn, transferFuns);

//        assertEquals(startSet, cfg.getProperty(node1, propertyName));
//        assertEquals(startSet.__remove(ImmutableTuple2.of("y", Optional.empty())).__insert(ImmutableTuple2.of("y", Optional.of(1))), cfg.getProperty(node2, propertyName));
//        assertEquals(startSet, cfg.getProperty(node3, propertyName));
//        assertEquals(startSet, cfg.getProperty(node4, propertyName));
//        assertEquals(startSet, cfg.getProperty(node5, propertyName));
//        assertEquals(startSet, cfg.getProperty(node6, propertyName));
    }
}

class CFGNode implements ICFGNode, IApplTerm {
    private final String resource;
    private final String name;
    private final ImmutableClassToInstanceMap<Object> attachments;

    public CFGNode(String resource, String name) {
        this(resource, name, ImmutableClassToInstanceMap.builder().build());
    }

    public CFGNode(String resource, String name, TermIndex index) {
        this(resource, name, ImmutableClassToInstanceMap.builder().put(TermIndex.class, index).build());
    }

    private CFGNode(String resource, String name, ImmutableClassToInstanceMap<Object> attachments) {
        this.resource = resource;
        this.name = name;
        this.attachments = attachments;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((resource == null) ? 0 : resource.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CFGNode other = (CFGNode) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (resource == null) {
            if (other.resource != null)
                return false;
        } else if (!resource.equals(other.resource))
            return false;
        return true;
    }

    @Override
    public boolean isGround() {
        return true;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public IApplTerm withLocked(boolean locked) {
        return null;
    }

    @Override
    public Multiset<ITermVar> getVars() {
        return ImmutableMultiset.of();
    }

    @Override
    public ImmutableClassToInstanceMap<Object> getAttachments() {
        return this.attachments;
    }

    @Override
    public IApplTerm withAttachments(ImmutableClassToInstanceMap<Object> value) {
        return new CFGNode(this.resource, this.name, value);
    }

    @Override
    public <T> T match(Cases<T> cases) {
        return cases.caseAppl(this);
    }

    @Override
    public <T, E extends Throwable> T matchOrThrow(CheckedCases<T, E> cases) throws E {
        return cases.caseAppl(this);
    }

    @Override
    public String getOp() {
        return "CFGNode";
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public List<ITerm> getArgs() {
        return Arrays.asList(TB.newString(resource), TB.newString(name));
    }
}