package mb.flowspec.comlan18.flowspec;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.project.IProject;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.analysis.ISpoofaxAnalyzeResult;
import org.metaborg.spoofax.core.stratego.IStrategoCommon;
import org.metaborg.spoofax.core.unit.ISpoofaxInputUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxParseUnit;
import org.metaborg.spoofax.core.unit.ParseContrib;
import org.metaborg.util.concurrent.IClosableLock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import mb.flowspec.comlan18.BaseBenchmark;
import mb.flowspec.comlan18.SpoofaxModuleExtension;
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

public abstract class FlowSpecBenchmark extends BaseBenchmark {
    protected FlowSpecBenchmark(URL inputURL) {
        super(inputURL);
    }

    protected ILanguageImpl language;
    protected Spoofax spoofax;
    private ISpoofaxParseUnit parseUnit;
    private FileObject inputFileObject;
    private IStrategoCommon strategoCommon;
    private IProject project;
    private IStrategoTuple input;

    @Setup public void setupSpoofax() throws MetaborgException, URISyntaxException {
        spoofax = new Spoofax(new SpoofaxModuleExtension());
        FileObject languageZip = spoofax.resourceService
            .resolve(BaseBenchmark.class.getResource("/stratego.typed-0.1.0-SNAPSHOT.spoofax-language").toURI());

        inputFileObject = spoofax.resourceService.resolve(inputURL.getPath());
        project = spoofax.projectService.get(inputFileObject);
        language = spoofax.languageDiscoveryService.languageFromArchive(languageZip);

        final ISpoofaxInputUnit inputUnit = spoofax.unitService.inputUnit(inputFileObject, "", language, null);
        parseUnit = spoofax.unitService.parseUnit(inputUnit, new ParseContrib(ctree));

        strategoCommon = spoofax.strategoCommon;

        final StrategoBlob result = new StrategoBlob(emptyResult());
        input = spoofax.termFactoryService.getGeneric().makeTuple(ctree, result);
    }

    @Benchmark public IStrategoTerm bench() throws MetaborgException, InterruptedException {
        IContext context =
            spoofax.contextService.get(inputFileObject, spoofax.projectService.get(inputFileObject), language);
        try(IClosableLock lock = context.write()) {
            ISpoofaxAnalyzeResult analysisResult = spoofax.analysisService.analyze(parseUnit, context);
            return analysisResult.result().ast();
        }
    }

    @Benchmark public IStrategoTerm benchSmarter() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, language);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(language, context, input, "benchmark-flowspec-analysis");
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
