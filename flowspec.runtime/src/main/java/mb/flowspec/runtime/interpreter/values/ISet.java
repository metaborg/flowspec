package mb.flowspec.runtime.interpreter.values;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.List;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;

public interface ISet<K extends ITerm> extends IApplTerm {
    io.usethesource.capsule.Set.Immutable<K> getSet();

    default boolean isGround() {
        return true;
    }

    default Multiset<ITermVar> getVars() {
        return ImmutableMultiset.of();
    }

    ImmutableClassToInstanceMap<Object> getAttachments();

    ISet<K> withAttachments(ImmutableClassToInstanceMap<Object> value);

    default <T> T match(ITerm.Cases<T> cases) {
        return cases.caseAppl(this);
    }

    default <T, E extends Throwable> T matchOrThrow(ITerm.CheckedCases<T, E> cases) throws E {
        return cases.caseAppl(this);
    }

    default String getOp() {
        return "Set";
    }

    default int getArity() {
        return 1;
    }

    default List<ITerm> getArgs() {
        return new ImmutableList.Builder<ITerm>().add(B.newList(this.getSet())).build();
    }
}