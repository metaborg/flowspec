package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.stratego.MatchTerm;
import meta.flowspec.java.stratego.ToIStrategoTerm;

public class Condition implements ToIStrategoTerm {
    public final Variable lhs;
    public final String relation;
    public final Variable rhs;

    /**
     * @param lhs
     * @param relation
     * @param rhs
     */
    public Condition(Variable lhs, String relation, Variable rhs) {
        this.lhs = lhs;
        this.relation = relation;
        this.rhs = rhs;
    }

    @Override
    public IStrategoTerm toIStrategoTerm(ITermFactory factory) {
        return factory.makeAppl(factory.makeConstructor("HasProp", 3), lhs.toIStrategoTerm(factory),
                factory.makeString(relation), rhs.toIStrategoTerm(factory));
    }

    public static class Utils {
        public static Optional<Condition> match(IStrategoTerm term) {
            return MatchTerm.applChildren(new StrategoConstructor("HasProp", 3), term)
                    .flatMap(children -> Variable.match(children[0]).flatMap(lhs -> MatchTerm.string(children[1])
                            .flatMap(rel -> Variable.match(children[2]).map(rhs -> new Condition(lhs, rel, rhs)))));
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Condition [lhs=" + lhs + ", relation=" + relation + ", rhs=" + rhs + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
        result = prime * result + ((relation == null) ? 0 : relation.hashCode());
        result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
        return result;
    }

    /* (non-Javadoc)
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
        Condition other = (Condition) obj;
        if (lhs == null) {
            if (other.lhs != null)
                return false;
        } else if (!lhs.equals(other.lhs))
            return false;
        if (relation == null) {
            if (other.relation != null)
                return false;
        } else if (!relation.equals(other.relation))
            return false;
        if (rhs == null) {
            if (other.rhs != null)
                return false;
        } else if (!rhs.equals(other.rhs))
            return false;
        return true;
    }

}
