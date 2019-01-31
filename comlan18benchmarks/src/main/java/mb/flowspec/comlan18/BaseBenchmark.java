package mb.flowspec.comlan18;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

import mb.nabl2.regexp.IAlphabet;
import mb.nabl2.regexp.IRegExp;
import mb.nabl2.regexp.impl.FiniteAlphabet;
import mb.nabl2.regexp.impl.RegExpBuilder;
import mb.nabl2.relations.IRelation;
import mb.nabl2.relations.RelationDescription;
import mb.nabl2.relations.terms.Relation;
import mb.nabl2.scopegraph.terms.ImmutableResolutionParameters;
import mb.nabl2.scopegraph.terms.Label;
import mb.nabl2.scopegraph.terms.ResolutionParameters;
import mb.nabl2.solver.Fresh;
import mb.nabl2.solver.ISolution;
import mb.nabl2.solver.ImmutableSolution;
import mb.nabl2.solver.ImmutableSolverConfig;
import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.spoofax.analysis.ImmutableSingleUnitResult;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(jvmArgsAppend = { "-Xms512m", "-Xmx1024m", "-Xss16m" })
public abstract class BaseBenchmark {
    final protected URL inputURL;
    protected IStrategoTerm ctree;

    protected BaseBenchmark(URL inputURL) {
        this.inputURL = inputURL;
    }

    @Setup public void setupInput() throws IOException {
        ITermFactory termFactory = new TermFactory();
        ctree = termFactory.parseFromString(readInputStream(inputURL.openStream()));
    }

    /**
     * source: https://stackoverflow.com/a/35446009
     */
    public static String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

    public static URL getResource(String name) {
        return BaseBenchmark.class.getResource(name);
    }

    protected static IResult emptyResult() {
        final Label labelD = Label.D;
        final IAlphabet<Label> labels = new FiniteAlphabet<>(labelD);
        final IRegExp<Label> pathWf = new RegExpBuilder<>(labels).emptySet();
        final IRelation.Immutable<Label> specificityOrder =
            Relation.Immutable.of(RelationDescription.STRICT_PARTIAL_ORDER);
        final ResolutionParameters resolutionParams =
            ImmutableResolutionParameters.of(labels, labelD, pathWf, specificityOrder);
        final ISolution solution = ImmutableSolution
            .of(ImmutableSolverConfig.of(resolutionParams, Collections.emptyMap(), Collections.emptyMap()));
        return ImmutableSingleUnitResult.of(Collections.emptyList(), solution, Optional.empty(), Fresh.Immutable.of());
    }
}
