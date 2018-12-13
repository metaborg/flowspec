package mb.flowspec.runtime.solver;

import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Optional;
import java.util.Map.Entry;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Map;
import mb.flowspec.runtime.interpreter.InitValues;
import mb.nabl2.terms.matching.TermMatch.IMatcher;

@Immutable
public abstract class StaticInfo {
    /// Strings are names of dataflow properties
    @Parameter public abstract BinaryRelation.Immutable<String, String> dependsOn();
    /// String is dataflow property name
    @Parameter public abstract Map.Immutable<String, Metadata<?>> metadata();
    @Parameter public abstract FunctionInfo functions();
    @Parameter public abstract LatticeInfo lattices();

    public StaticInfo addAll(StaticInfo other) {
        BinaryRelation.Transient<String, String> dependsOn = this.dependsOn().asTransient();
        other.dependsOn().entryIterator().forEachRemaining(e -> {
            dependsOn.__insert(e.getKey(), e.getValue());
        });
        Map.Transient<String, Metadata<?>> metadata = this.metadata().asTransient();
        other.metadata().entryIterator().forEachRemaining(e -> {
            Metadata<?> md = metadata.get(e.getKey());
            if(md == null) {
                metadata.__put(e.getKey(), e.getValue());
            } else {
                metadata.__put(e.getKey(), md.addAll(e.getValue()));
            }
        });
        FunctionInfo functions = this.functions().addAll(other.functions());
        LatticeInfo lattices = this.lattices().addAll(other.lattices());
        return ImmutableStaticInfo.of(dependsOn.freeze(), metadata.freeze(), functions, lattices);
    }

    public void init(InitValues initValues) {
        this.functions().functions().values().stream().forEach(f -> f.init(initValues));
        this.lattices().latticeDefs().values().stream().forEach(l -> l.init(initValues));
        for (Entry<String, Metadata<?>> e : metadata().entrySet()) {
            e.getValue().transferFunctions().valueIterator().forEachRemaining(tf -> {
                tf.init(initValues);
            });
        }
    }

    public static IMatcher<StaticInfo> match(String moduleName) {
        return (term, unifier) ->
            Optional.of(
                M.tuple3(M.term(), LatticeInfo.match(), FunctionInfo.match(), (t, tf, l, f) ->
                    TransferFunctionInfo.match(l, moduleName)
                        .match(tf, unifier)
                        .map(tf1 -> (StaticInfo) ImmutableStaticInfo.of(tf1.dependsOn(), tf1.metadata(), f, l))
                )
                .flatMap(i -> i)
                .match(term, unifier)
                .orElseThrow(() -> new ParseException("Parse error on reading Static Info"))
            );
    }

    public static StaticInfo of() {
        return ImmutableStaticInfo.of(BinaryRelation.Immutable.of(), Map.Immutable.of(), ImmutableFunctionInfo.of(), ImmutableLatticeInfo.of());
    }
}
