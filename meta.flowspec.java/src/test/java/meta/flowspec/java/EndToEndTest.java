package meta.flowspec.java;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.pcollections.Empty;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;

import meta.flowspec.java.interpreter.TransferFunction;
import meta.flowspec.java.interpreter.TransferFunctionAppl;
import meta.flowspec.java.interpreter.Where;
import meta.flowspec.java.interpreter.expressions.ReadPropNode;
import meta.flowspec.java.interpreter.expressions.SetLiteralNode;
import meta.flowspec.java.interpreter.locals.ArgToVarNode;
import meta.flowspec.java.interpreter.locals.ReadVarNodeGen;
import meta.flowspec.java.interpreter.locals.WriteVarNode;
import meta.flowspec.java.lattice.CompleteLattice;
import meta.flowspec.java.lattice.FullSetLattice;
import meta.flowspec.java.pcollections.MapSetPRelation;
import meta.flowspec.java.solver.ImmutableMetadata;
import meta.flowspec.java.solver.MFP2;
import meta.flowspec.java.solver.Metadata;
import meta.flowspec.java.solver.Metadata.Direction;
import meta.flowspec.java.solver.Type;
import meta.flowspec.nabl2.controlflow.ICFGNode;
import meta.flowspec.nabl2.controlflow.impl.ControlFlowGraph;
import meta.flowspec.nabl2.util.tuples.ImmutableTuple2;
import meta.flowspec.nabl2.util.tuples.Tuple2;

public class EndToEndTest {
    @Test
    public void emptyCFG() {
        ControlFlowGraph<ICFGNode> cfg = new ControlFlowGraph<>();
        MFP2.<ICFGNode>intraProcedural(cfg, Empty.map(), new MapSetPRelation<>(), Empty.map());
        assertEquals(cfg, new ControlFlowGraph<>());
    }

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
                    new SetLiteralNode(HashTreePSet.singleton(42))));

        cfg.addDirectEdge(start, nodeA);
        cfg.addDirectEdge(nodeA, nodeB);
        cfg.addDirectEdge(nodeB, end);

        cfg.addTFAppl(nodeA, propertyName, new TransferFunctionAppl(0, new Object[] {}));
        cfg.addTFAppl(nodeB, propertyName, new TransferFunctionAppl(1, new Object[] {}));

        PMap<String, Metadata> propMetadata = Empty.map();
        propMetadata = propMetadata.plus(propertyName, ImmutableMetadata.of(Direction.Backward, (CompleteLattice) new FullSetLattice<Integer>(), new Type()));

        MapSetPRelation<String, String> propDependsOn = new MapSetPRelation<>();

        PMap<String, TransferFunction[]> transferFuns = Empty.map();
        transferFuns = transferFuns.plus(propertyName, new TransferFunction[] {transA, transB});

        MFP2.<ICFGNode>intraProcedural(cfg, propMetadata, propDependsOn, transferFuns);

        assertEquals(HashTreePSet.singleton(42), cfg.getProperty(nodeB, propertyName));
        assertEquals(HashTreePSet.singleton(42), cfg.getProperty(nodeA, propertyName));
    }

    @Ignore("incomplete")
    @Test
    public void reachingDefinitionsExample1() {
        ControlFlowGraph<ICFGNode> cfg = new ControlFlowGraph<>();

        String propertyName = "C";

        CFGNode start = new CFGNode("", "start");
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
        PSet<Tuple2<String, Optional<Integer>>> startSet = HashTreePSet.from(Arrays.asList(
                ImmutableTuple2.of("x", unknown),
                ImmutableTuple2.of("y", unknown),
                ImmutableTuple2.of("z", unknown)));
        @SuppressWarnings({ "unchecked", "rawtypes" })
        TransferFunction trans1 = 
            new TransferFunction(
                fdTrans1, 
                new ArgToVarNode[] {
                    new ArgToVarNode(0, fdTrans1.addFrameSlot(successorNodeName, FrameSlotKind.Object))
                }, 
                new Where(
                    new WriteVarNode[] {}, 
                    new SetLiteralNode((PSet) startSet)));

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

        PMap<String, Metadata> propMetadata = Empty.map();
        propMetadata = propMetadata.plus(propertyName, ImmutableMetadata.of(Direction.Forward, (CompleteLattice) new FullSetLattice<Optional<Integer>>(), new Type()));

        MapSetPRelation<String, String> propDependsOn = new MapSetPRelation<>();

        PMap<String, TransferFunction[]> transferFuns = Empty.map();
        transferFuns = transferFuns.plus(propertyName, new TransferFunction[] {trans1, trans2});

        MFP2.<ICFGNode>intraProcedural(cfg, propMetadata, propDependsOn, transferFuns);

        assertEquals(startSet, cfg.getProperty(node1, propertyName));
        assertEquals(startSet.minus(ImmutableTuple2.of("y", Optional.empty())).plus(ImmutableTuple2.of("y", Optional.of(1))), cfg.getProperty(node2, propertyName));
        assertEquals(startSet, cfg.getProperty(node3, propertyName));
        assertEquals(startSet, cfg.getProperty(node4, propertyName));
        assertEquals(startSet, cfg.getProperty(node5, propertyName));
        assertEquals(startSet, cfg.getProperty(node6, propertyName));
    }
}

class CFGNode implements ICFGNode {
    private final String resource;
    private final String name;

    public CFGNode(String resource, String name) {
        this.resource = resource;
        this.name = name;
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
}