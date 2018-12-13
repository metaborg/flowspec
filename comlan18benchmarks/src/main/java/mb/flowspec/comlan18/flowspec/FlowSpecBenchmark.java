package mb.flowspec.comlan18.flowspec;

import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.unit.ISpoofaxInputUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxParseUnit;
import org.metaborg.spoofax.core.unit.ParseContrib;
import org.metaborg.util.concurrent.IClosableLock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;

import mb.flowspec.comlan18.BaseBenchmark;
import mb.flowspec.comlan18.SpoofaxModuleExtension;

public abstract class FlowSpecBenchmark extends BaseBenchmark {
    protected FlowSpecBenchmark(URL inputURL) {
        super(inputURL);
    }

    protected ILanguageImpl language;
    protected Spoofax spoofax;
    private ISpoofaxInputUnit inputUnit;
    private ISpoofaxParseUnit parseUnit;
    private FileObject inputFileObject;
    private IContext context;

    @Setup
    public void setupSpoofax() throws MetaborgException, URISyntaxException {
        spoofax = new Spoofax(new SpoofaxModuleExtension());
        FileObject languageZip = spoofax.resourceService.resolve(BaseBenchmark.class.getResource("/stratego.typed-0.1.0-SNAPSHOT.spoofax-language").toURI());
        language = spoofax.languageDiscoveryService.languageFromArchive(languageZip);
        inputFileObject = spoofax.resourceService.resolve(inputURL.getPath());
        inputUnit = spoofax.unitService.inputUnit(inputFileObject, "", language, null);
        parseUnit = spoofax.unitService.parseUnit(inputUnit, new ParseContrib(ctree));
        context = spoofax.contextService.get(inputFileObject, spoofax.projectService.get(inputFileObject), language);
    }

    @Benchmark
    public void bench() throws MetaborgException, InterruptedException {
        try(IClosableLock lock = context.write()) {
            spoofax.analysisService.analyze(parseUnit, context);
        }
    }
}
