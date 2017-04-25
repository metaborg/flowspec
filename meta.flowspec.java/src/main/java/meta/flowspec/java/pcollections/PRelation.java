package meta.flowspec.java.pcollections;

import java.util.Map;
import java.util.Set;

import org.pcollections.PSet;

public interface PRelation<L,R> {
    PRelation<L,R> plus(L lhs, R rhs);
    PRelation<L,R> plusAll(PRelation<? extends L, ? extends R> map);
    PRelation<L,R> plusAll(Map<? extends L, ? extends R> map);
    PRelation<L,R> minus(L lhs, R rhs);
    PRelation<L,R> minusAll(PRelation<? extends L, ? extends R> map);
    PRelation<L,R> minusAll(Map<? extends L, ? extends R> map);

    int size();
    boolean isEmpty();
    boolean contains(L lhs, R rhs);
    boolean containsLhs(L lhs);
    boolean containsRhs(R rhs);
    PSet<R> getRhsSet(L lhs);
    Set<L> lhsSet();
    PSet<R> rhsSet();
    Set<Map.Entry<L, R>> entrySet();

    PRelation<R,L> reverse();
}
