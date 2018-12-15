package mb.flowspec.runtime.solver;

import mb.nabl2.terms.matching.TermMatch.IMatcher;
import static mb.nabl2.terms.matching.TermMatch.M;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public abstract class SimpleType extends Type {
    @Parameter abstract SimpleTypeEnum simpleType();

    public static IMatcher<SimpleTypeEnum> match() {
        return M.cases(
                M.appl0("Name", appl -> SimpleTypeEnum.Name),
                M.appl0("Term", appl -> SimpleTypeEnum.Term),
                M.appl0("Index", appl -> SimpleTypeEnum.Index),
                M.appl0("Int", appl -> SimpleTypeEnum.Int),
                M.appl0("String", appl -> SimpleTypeEnum.String),
                M.appl0("Float", appl -> SimpleTypeEnum.Float),
                M.appl0("Bool", appl -> SimpleTypeEnum.Bool)
            );
    }

    public static IMatcher<SimpleType> matchSimpleType() {
        return match().map(ImmutableSimpleType::of);
    }

    public enum SimpleTypeEnum {
        Name,
        Term,
        Index,
        Int,
        String,
        Float,
        Bool
    }
}
