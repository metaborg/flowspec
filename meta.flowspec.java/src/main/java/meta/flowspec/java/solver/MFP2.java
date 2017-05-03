package meta.flowspec.java.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pcollections.Empty;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import meta.flowspec.java.Pair;
import meta.flowspec.java.ast.Condition;
import meta.flowspec.java.ast.ConditionalRhs;
import meta.flowspec.java.ast.Rhs;
import meta.flowspec.java.ast.TermIndex;
import meta.flowspec.java.ast.Value;
import meta.flowspec.java.ast.types.Type;
import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.pcollections.MapSetPRelation;
import meta.flowspec.java.pcollections.PRelation;

public abstract class MFP2 {
    public static PRelation<Pair<String, TermIndex>, Pair<Rhs, Rhs>> intraProcedural(
            PRelation<Pair<String, TermIndex>, ConditionalRhs> conditional,
            @SuppressWarnings("rawtypes") Map<String, Type> types) {
        final Map<Pair<String, TermIndex>, Value> analysis = new HashMap<>();
        PSet<Pair<String, TermIndex>> workList;
        PRelation<String, String> mayInfluence = new MapSetPRelation<String, String>();

        for (Map.Entry<Pair<String, TermIndex>, ConditionalRhs> e : conditional.entrySet()) {
            // initial ("extremal") values
            if (e.getValue().conditions.isEmpty()) {
                conditional = conditional.minus(e.getKey(), e.getValue());
                analysis.put(e.getKey(), (TermIndex) e.getValue().value);
            }
            // derivation of dependencies
            else {
                final String thisProp = e.getKey().left();
                for (Condition c : e.getValue().conditions) {
                    mayInfluence = mayInfluence.plus(c.relation, thisProp);
                }
            }
        }

        List<String> propTopoOrder = MapSetPRelation.topoSort(mayInfluence).get();

        for (String prop : propTopoOrder) {
            workList = Empty.set();
//            final Lattice l = types.get(prop).getLattice();
            if (!(types.get(prop) instanceof meta.flowspec.java.ast.types.Set<?>)) {
                throw new RuntimeException("Stubbing out types support until the surface syntax issues around non-set "
                        + "types are resolved");
            }
            for (Pair<String, TermIndex> p : conditional.lhsSet()) {
                if (p.left().equals(prop)) {
                    workList = workList.plus(p);
                }
            }
            while (!workList.isEmpty()) {
                final Pair<String, TermIndex> from = workList.iterator().next();
                workList = workList.minus(from);
                meta.flowspec.java.ast.Set transferred = new meta.flowspec.java.ast.Set<>(Empty.set());
                for (final ConditionalRhs transfer : conditional.getRhsSet(from)) {
                    transferred = transferred.plusAll(doTransfer(transfer, analysis));
                }
            }
        }

        return null;
    }

    private static Collection doTransfer(ConditionalRhs transfer, Map<Pair<String, TermIndex>, Value> analysis) {
        
        return null;
    }
    
    
}
