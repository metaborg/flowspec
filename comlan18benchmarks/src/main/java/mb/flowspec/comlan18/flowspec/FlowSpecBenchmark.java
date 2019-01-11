package mb.flowspec.comlan18.flowspec;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.project.IProject;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.stratego.IStrategoCommon;
import org.metaborg.spoofax.core.stratego.primitive.flowspec.FS_solve;
import org.metaborg.util.concurrent.IClosableLock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import mb.flowspec.comlan18.BaseBenchmark;
import mb.flowspec.comlan18.SpoofaxModuleExtension;
import mb.flowspec.controlflow.ControlFlowGraphReader;
import mb.flowspec.controlflow.FlowSpecSolution;
import mb.flowspec.controlflow.IFlowSpecSolution;
import mb.flowspec.primitives.AnalysisPrimitive;
import mb.flowspec.runtime.interpreter.InterpreterBuilder;
import mb.flowspec.runtime.solver.FixedPoint;
import mb.flowspec.terms.B;
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
import mb.nabl2.stratego.StrategoBlob;
import mb.nabl2.stratego.StrategoTermIndices;

public abstract class FlowSpecBenchmark extends BaseBenchmark {
    protected FlowSpecBenchmark(URL inputURL) {
        super(inputURL);
    }

    protected ILanguageImpl language;
    protected Spoofax spoofax;
    private FileObject inputFileObject;
    private IStrategoCommon strategoCommon;
    private IProject project;
    private IStrategoTuple input;
    private IStrategoTerm annotated;
    private IStrategoTerm cfgList;
    private IResult result;
    private IResult cfgBuilt;
    private List<String> propertyNames;

    @Setup public void setupSpoofax() throws MetaborgException, URISyntaxException, InterruptedException {
        spoofax = new Spoofax(new SpoofaxModuleExtension());
        FileObject languageZip = spoofax.resourceService
            .resolve(BaseBenchmark.class.getResource("/stratego.typed-0.1.0-SNAPSHOT.spoofax-language").toURI());

        inputFileObject = spoofax.resourceService.resolve(inputURL.getPath());
        project = spoofax.projectService.get(inputFileObject);
        language = spoofax.languageDiscoveryService.languageFromArchive(languageZip);

        strategoCommon = spoofax.strategoCommon;

        result = emptyResult();
        final ITermFactory tf = spoofax.termFactoryService.getGeneric();
        annotated = StrategoTermIndices.index(ctree, "benchmarking", tf);
        input = B.tuple(annotated, new StrategoBlob(result));
        cfgList = benchCFGStr();
        cfgBuilt = benchCFGJava();
        propertyNames = Arrays.asList("reachingDefinitions");
    }

    @Benchmark public IStrategoTerm benchCFGPrimitive() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, language);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(language, context, input, "benchmark-flowspec-cfg-create");
        }
    }

    @Benchmark public IStrategoTerm benchCFGStr() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, language);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(language, context, annotated, "flowspec--generate-cfg");
        }
    }

    @Benchmark public IResult benchCFGJava() throws MetaborgException, InterruptedException {
        ControlFlowGraphReader cfgReader = ControlFlowGraphReader.build(cfgList);
        return result.withCustomAnalysis(FlowSpecSolution.of(result.solution(), cfgReader.cfg(), cfgReader.tfAppls()));
    }

    @Benchmark public IResult benchDFSolving() throws MetaborgException, InterruptedException {
        IFlowSpecSolution sol = AnalysisPrimitive.getFSSolution(cfgBuilt).get();
        FixedPoint solver = new FixedPoint();
        final InterpreterBuilder interpBuilder = spoofax.injector.getInstance(FS_solve.class).getFlowSpecInterpreterBuilder(language);
        final ISolution solution = solver.entryPoint(sol, interpBuilder, propertyNames);
        return cfgBuilt.withSolution(solution);
    }

    @Benchmark public IStrategoTerm bench() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, language);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(language, context, input, "benchmark-flowspec-analysis");
        }
    }

    @Benchmark public IStrategoTerm bench2() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, language);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(language, context, input, "benchmark-flowspec-analysis2");
        }
    }

    public IStrategoTerm test() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, language);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(language, context, input, "test-flowspec-analysis");
        }
    }

    private static IResult emptyResult() {
        final Label labelD = Label.D;
        final IAlphabet<Label> labels = new FiniteAlphabet<>(labelD);
        final IRegExp<Label> pathWf = new RegExpBuilder<>(labels).emptySet();
        final IRelation.Immutable<Label> specificityOrder = Relation.Immutable.of(RelationDescription.PARTIAL_ORDER);
        final ResolutionParameters resolutionParams =
            ImmutableResolutionParameters.of(labels, labelD, pathWf, specificityOrder);
        final ISolution solution = ImmutableSolution
            .of(ImmutableSolverConfig.of(resolutionParams, Collections.emptyMap(), Collections.emptyMap()));
        return ImmutableSingleUnitResult.of(Collections.emptyList(), solution, Optional.empty(), Fresh.Immutable.of());
    }
}
