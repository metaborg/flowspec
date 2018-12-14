package mb.flowspec.comlan18;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.metaborg.core.MetaborgException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import mb.flowspec.comlan18.flowspec.FSLibSpoofaxBenchmark;
import mb.flowspec.comlan18.stratego.StrLibSpoofaxBenchmark;

public class LibSpoofaxTest {
    FSLibSpoofaxBenchmark flowspecBenchmark;
    StrLibSpoofaxBenchmark strategoBenchmark;

    @Before
    public void setup() throws IOException, MetaborgException, URISyntaxException {
        flowspecBenchmark = new FSLibSpoofaxBenchmark();
        strategoBenchmark = new StrLibSpoofaxBenchmark();
        flowspecBenchmark.setupInput();
        flowspecBenchmark.setupSpoofax();
        strategoBenchmark.setupInput();
    }

    @Test
    @Ignore
    public void testBenchResults() throws MetaborgException, InterruptedException {
        IStrategoTerm flowspecResult = flowspecBenchmark.bench();
        IStrategoTerm strategoResult = strategoBenchmark.bench();
        assertEquals(flowspecResult, strategoResult);
    }

    @Test
    @Ignore
    public void testBenchSmarterResults() throws MetaborgException, InterruptedException {
        IStrategoTerm flowspecResult = flowspecBenchmark.benchSmarter();
        IStrategoTerm strategoResult = strategoBenchmark.bench();
        assertEquals(flowspecResult, strategoResult);
    }
}
