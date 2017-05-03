package meta.flowspec.java.ast;

import java.util.Collection;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import meta.flowspec.java.stratego.BuildSolverTerms;

public class Set<L extends Value> implements Value {

    public final PSet<L> set;

    public Set(PSet<L> set) {
        this.set = set;
    }

    public Set(L value) {
        this.set = HashTreePSet.singleton(value);
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return BuildSolverTerms.makeList(this.set.stream().map(l -> l.toIStrategoTerm(factory)), factory);
    }
    
    /**
     * @param e a single element to be added to the set
     * @return the set with that element
     * @see org.pcollections.PSet#plus(java.lang.Object)
     */
    public Set<L> plus(L e) {
        return new Set<>(set.plus(e));
    }

    /**
     * @param list
     * @return
     * @see org.pcollections.PSet#plusAll(java.util.Collection)
     */
    public Set<L> plusAll(Collection<? extends L> list) {
        return new Set<>(set.plusAll(list));
    }

    /**
     * @param e a single element to be removed from the set
     * @return the set without that element
     * @see org.pcollections.PSet#minus(java.lang.Object)
     */
    public Set<L> minus(Object e) {
        return new Set<>(set.minus(e));
    }

    /**
     * @param list
     * @return
     * @see org.pcollections.PSet#minusAll(java.util.Collection)
     */
    public Set<L> minusAll(Collection<?> list) {
        return new Set<>(set.minusAll(list));
    }
}
