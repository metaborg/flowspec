package meta.flowspec.java.ast;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

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

}
