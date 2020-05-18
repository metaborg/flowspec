package mb.flowspec.comlan18.flowspec;

import java.net.URISyntaxException;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.project.IProject;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.stratego.IStrategoCommon;
import org.metaborg.util.concurrent.IClosableLock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import mb.flowspec.comlan18.BaseBenchmark;
import mb.flowspec.comlan18.SpoofaxModuleExtension;
import mb.flowspec.terms.B;
import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.stratego.StrategoBlob;
import mb.nabl2.stratego.StrategoTermIndices;

public abstract class FlowSpecGmBenchmark extends BaseBenchmark {
    protected ILanguageImpl backend;
    protected Spoofax spoofax;
    private FileObject inputFileObject;
    private IStrategoCommon strategoCommon;
    private IProject project;
    private IStrategoTerm annotated;
    private IResult result;
    private ITermFactory tf;
    private ILanguageImpl frontend;

    protected FlowSpecGmBenchmark(String inputResource) {
        super(getResource(inputResource));
    }

    @Setup public void setupSpoofax() throws MetaborgException, URISyntaxException, InterruptedException {
        spoofax = new Spoofax(new SpoofaxModuleExtension());
        FileObject frontendZip = spoofax.resourceService.resolve(getResource("/gm_lang-0.1.0-FRONTEND-SNAPSHOT.spoofax-language").toURI());
        FileObject backendZip = spoofax.resourceService.resolve(getResource("/pgx_gm_java-0.1.0-BACKEND-SNAPSHOT.spoofax-language").toURI());

        inputFileObject = spoofax.resourceService.resolve(inputURL.getPath());
        project = spoofax.projectService.get(inputFileObject);
        // load frontend first
        frontend = spoofax.languageDiscoveryService.languageFromArchive(frontendZip);
        backend = spoofax.languageDiscoveryService.languageFromArchive(backendZip);

        strategoCommon = spoofax.strategoCommon;

        result = emptyResult();
        tf = spoofax.termFactory;
        annotated = StrategoTermIndices.index(ctree, "benchmarking", tf);
    }

    public IStrategoTuple createInput(String analysisName) {
        return B.tuple(annotated, B.list(B.string(analysisName)), new StrategoBlob(result));
    }

    @Benchmark public IStrategoTerm benchReachingDefinitions() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, frontend);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(backend, context, createInput("reaching"), "benchmark-flowspec-analysis");
        }
    }

    @Benchmark public IStrategoTerm benchLiveVariables() throws MetaborgException, InterruptedException {
        final IContext context = spoofax.contextService.get(inputFileObject, project, frontend);
        try(IClosableLock lock = context.read()) {
            return strategoCommon.invoke(backend, context, createInput("live"), "benchmark-flowspec-analysis");
        }
    }
}
