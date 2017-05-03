package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.ast.Value;
import meta.flowspec.java.lattice.CompleteLattice;
import meta.flowspec.java.lattice.FullSetLattice;
import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class Set<L extends Value> extends Type<meta.flowspec.java.ast.Set<L>> {
    public final Type<L> element;

    public Set(Type<L> element) {
        this.element = element;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Set", 1), element.toIStrategoTerm(factory));
    }

    public static class Utils {
        @SuppressWarnings("unchecked")
        public static <L extends Value> Optional<Set<L>> match(IStrategoTerm term) {
            return MatchTerm.applChildren(new StrategoConstructor("Set", 1), term)
                    .flatMap(children -> Type.Utils.match(children[0]).map(Set::new));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Lattice<meta.flowspec.java.ast.Set<L>> getLattice() {
        return (Lattice<meta.flowspec.java.ast.Set<L>>) this.internalGetLattice();
    }
    
    @SuppressWarnings("rawtypes")
    public CompleteLattice internalGetLattice() {
        return new CompleteLattice<meta.flowspec.java.ast.Set<L>>() {
            private FullSetLattice<L> lattice = new FullSetLattice<>();

            /**
             * @return
             * @see meta.flowspec.java.lattice.FullSetLattice#top()
             */
            public meta.flowspec.java.ast.Set<L> top() {
                return new meta.flowspec.java.ast.Set<L>(lattice.top());
            }

            /**
             * @return
             * @see meta.flowspec.java.lattice.FullSetLattice#bottom()
             */
            public meta.flowspec.java.ast.Set<L> bottom() {
                return new meta.flowspec.java.ast.Set<L>(lattice.bottom());
            }

            /**
             * @param one
             * @param other
             * @return
             * @see meta.flowspec.java.lattice.FullSetLattice#lte(org.pcollections.PSet, org.pcollections.PSet)
             */
            public boolean lte(meta.flowspec.java.ast.Set<L> one, meta.flowspec.java.ast.Set<L> other) {
                return lattice.lte(one.set, other.set);
            }

            /**
             * @param one
             * @param other
             * @return
             * @see meta.flowspec.java.lattice.FullSetLattice#glb(org.pcollections.PSet, org.pcollections.PSet)
             */
            public meta.flowspec.java.ast.Set<L> glb(meta.flowspec.java.ast.Set<L> one, meta.flowspec.java.ast.Set<L> other) {
                return new meta.flowspec.java.ast.Set<L>(lattice.glb(one.set, other.set));
            }

            /**
             * @param one
             * @param other
             * @return
             * @see meta.flowspec.java.lattice.FullSetLattice#lub(org.pcollections.PSet, org.pcollections.PSet)
             */
            public meta.flowspec.java.ast.Set<L> lub(meta.flowspec.java.ast.Set<L> one, meta.flowspec.java.ast.Set<L> other) {
                return new meta.flowspec.java.ast.Set<L>(lattice.lub(one.set, other.set));
            }

            /**
             * @return
             * @see java.lang.Object#hashCode()
             */
            public int hashCode() {
                return lattice.hashCode();
            }

            /**
             * @param obj
             * @return
             * @see java.lang.Object#equals(java.lang.Object)
             */
            public boolean equals(Object obj) {
                return lattice.equals(obj);
            }

            /**
             * @return
             * @see java.lang.Object#toString()
             */
            public String toString() {
                return lattice.toString();
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Set<L> other = (Set<L>) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Set [element=" + element + "]";
    }

}
