package meta.flowspec.java.ast;

import java.util.Optional;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoConstructor;

import meta.flowspec.java.stratego.MatchTerm;

public class TermIndexUtils {
    public static Optional<TermIndex> fromIStrategoTerm(IStrategoTerm term) {
        return MatchTerm.applChildren(new StrategoConstructor("TermIndex", 2), term).flatMap(children -> MatchTerm
                .string(children[0]).flatMap(s -> MatchTerm.integer(children[1]).map(i -> new TermIndex(s, i))));
    }
}
