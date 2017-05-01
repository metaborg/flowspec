package meta.flowspec.java;

import java.util.ArrayList;
import java.util.List;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import meta.flowspec.java.ast.ConditionalValue;
import meta.flowspec.java.ast.TermIndex;
import meta.flowspec.java.ast.Value;
import meta.flowspec.java.ast.types.Type;
import meta.flowspec.java.pcollections.MapSetPRelation;
import meta.flowspec.java.pcollections.PRelation;
import meta.flowspec.java.stratego.MatchSolverTerms;
import meta.flowspec.java.stratego.MatchTerm;
import meta.flowspec.java.stratego.TermMatchException;

public class FS_solver extends AbstractPrimitive {
    private static final ILogger logger = LoggerUtils.logger(FS_solver.class);

    public FS_solver() {
        super(FS_solver.class.getSimpleName(), 0, 1);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        // PropName -> TermIndex -> ResultValue*
        final IStrategoTerm current = tvars[0];

        try {
            IStrategoTuple tuple = MatchTerm.tuple(current).orElseThrow(() -> new TermMatchException("tuple", current.toString()));
            if (tuple.getSubtermCount() != 2) {
                throw new TermMatchException("tuple of 2", current.toString());
            }
            List<IStrategoTerm> typedefs = MatchTerm.list(tuple.getSubterm(0)).orElseThrow(() -> new TermMatchException("list", current.getSubterm(1).toString()));
            List<Pair<String, Type>> types = new ArrayList<>();
            for (IStrategoTerm td: typedefs) {
                types.add(MatchSolverTerms.getTypeDef(td));
            }
            List<IStrategoTerm> conds = MatchTerm.list(current.getSubterm(1)).orElseThrow(() -> new TermMatchException("list", current.getSubterm(1).toString()));
            List<Pair<Pair<String, TermIndex>, ConditionalValue>> pairs = new ArrayList<>();
            for (IStrategoTerm cond : conds) {
                pairs.add(MatchSolverTerms.getPropConstraint(cond));
            }
            PRelation<Pair<String, TermIndex>, Value> simple = new MapSetPRelation<>();
            PRelation<Pair<String, TermIndex>, ConditionalValue> conditional = new MapSetPRelation<>();
            for (Pair<Pair<String, TermIndex>, ConditionalValue> pair : pairs) {
                Pair<String, TermIndex> key = pair.left();
                ConditionalValue value = pair.right();
                if (value.conditions.isEmpty()) {
                    simple = simple.plus(key, value.value);
                } else {
                    conditional = conditional.plus(key, value);
                }
            }
        } catch (TermMatchException e) {
            logger.warn("Did not receive well-formed input: " + e.getMessage());
            return false;
        }
        return true;
    }

}
