package mb.flowspec.comlan18;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.metaborg.core.MetaborgException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;

import mb.flowspec.comlan18.flowspec.FSLibSpoofaxBenchmark;
import mb.flowspec.comlan18.stratego.StrLibSpoofaxBenchmark;

public class LibSpoofaxTest {
    FSLibSpoofaxBenchmark flowspecBenchmark;
    StrLibSpoofaxBenchmark strategoBenchmark;
    IStrategoTerm expectedFromFlowSpec;

    @Before public void setup() throws IOException, MetaborgException, URISyntaxException, InterruptedException {
        flowspecBenchmark = new FSLibSpoofaxBenchmark();
        strategoBenchmark = new StrLibSpoofaxBenchmark();
        flowspecBenchmark.setupInput();
        flowspecBenchmark.setupSpoofax();
        strategoBenchmark.setupInput();
        expectedFromFlowSpec = new TermFactory().parseFromString(BaseBenchmark.readInputStream(FSLibSpoofaxBenchmark.class.getResource("/FSLibSpoofaxExpected.ctree").openStream()));
    }

    @Test public void regressionTestBench() throws MetaborgException, InterruptedException {
        IStrategoTerm actual = flowspecBenchmark.test();
        assertEquals(expectedFromFlowSpec, actual);
    }

    @Test public void regressionTestBench2() throws MetaborgException, InterruptedException {
        IStrategoTerm expected = flowspecBenchmark.test();
        IStrategoTerm actual = flowspecBenchmark.test2();
        assertEquals(expected, actual);
    }

    @Test public void testBenchRuns() throws MetaborgException, InterruptedException {
        flowspecBenchmark.benchCFGPrimitive();
        flowspecBenchmark.benchDFSolving();
    }
}
