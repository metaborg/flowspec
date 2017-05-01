package meta.flowspec.java.ast.types;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.ImmutablePair;
import meta.flowspec.java.lattice.CompleteLattice;
import meta.flowspec.java.lattice.Lattice;
import meta.flowspec.java.stratego.MatchTerm;

public class Tuple<L, R> extends Type<ImmutablePair<L, R>> {
    public final Type<L> left;
    public final Type<R> right;

    public Tuple(Type<L> left, Type<R> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("Tuple", 2), left.toIStrategoTerm(factory),
                right.toIStrategoTerm(factory));
    }

    static class Utils {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static Optional<Tuple> match(IStrategoTerm term) {
            return MatchTerm.applChildren(new StrategoConstructor("Tuple", 2), term)
                    .flatMap(children -> Type.Utils.match(children[0])
                            .flatMap(left -> Type.Utils.match(children[1]).map(right -> new Tuple(left, right))));
        }
    }

    @Override
    public Lattice<ImmutablePair<L, R>> getLattice() {
        Lattice<L> ll = this.left.getLattice();
        Lattice<R> rl = this.right.getLattice();
        if (ll instanceof CompleteLattice<?> && rl instanceof CompleteLattice<?>) {
            CompleteLattice<L> lcl = (CompleteLattice<L>) ll;
            CompleteLattice<R> rcl = (CompleteLattice<R>) rl;
            return new CompleteLattice<ImmutablePair<L, R>>() {
                @Override
                public ImmutablePair<L, R> top() {
                    return ImmutablePair.of(lcl.top(), rcl.top());
                }

                @Override
                public ImmutablePair<L, R> bottom() {
                    return ImmutablePair.of(lcl.bottom(), rcl.bottom());
                }

                @Override
                public ImmutablePair<L, R> glb(ImmutablePair<L, R> one, ImmutablePair<L, R> other) {
                    return ImmutablePair.of(lcl.glb(one.left(), other.left()), rcl.glb(one.right(), other.right()));
                }

                @Override
                public ImmutablePair<L, R> lub(ImmutablePair<L, R> one, ImmutablePair<L, R> other) {
                    return ImmutablePair.of(lcl.lub(one.left(), other.left()), rcl.lub(one.right(), other.right()));
                }
            };
        } else {
            return new Lattice<ImmutablePair<L, R>>() {
                @Override
                public Optional<ImmutablePair<L, R>> partial_glb(ImmutablePair<L, R> one, ImmutablePair<L, R> other) {
                    Optional<L> left_glb = ll.partial_glb(one.left(), other.left());
                    Optional<R> right_glb = rl.partial_glb(other.right(), other.right());
                    return left_glb.flatMap(l -> right_glb.map(r -> ImmutablePair.of(l, r)));
                }

                @Override
                public Optional<ImmutablePair<L, R>> partial_lub(ImmutablePair<L, R> one, ImmutablePair<L, R> other) {
                    Optional<L> left_lub = ll.partial_lub(one.left(), other.left());
                    Optional<R> right_lub = rl.partial_lub(other.right(), other.right());
                    return left_lub.flatMap(l -> right_lub.map(r -> ImmutablePair.of(l, r)));
                }
            };
        }
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
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
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
        Tuple<L, R> other = (Tuple<L, R>) obj;
        if (left == null) {
            if (other.left != null)
                return false;
        } else if (!left.equals(other.left))
            return false;
        if (right == null) {
            if (other.right != null)
                return false;
        } else if (!right.equals(other.right))
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
        return "Tuple [left=" + left + ", right=" + right + "]";
    }

}
