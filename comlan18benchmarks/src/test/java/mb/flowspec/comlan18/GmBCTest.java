package mb.flowspec.comlan18;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.metaborg.core.MetaborgException;

import mb.flowspec.comlan18.flowspec.GmBC;

public class GmBCTest {
    GmBC gmbc;

    @Before public void setup() throws IOException, MetaborgException, URISyntaxException, InterruptedException {
        gmbc = new GmBC();
        gmbc.setupInput();
        gmbc.setupSpoofax();
    }

    @Test public void testBenchRuns() throws MetaborgException, InterruptedException {
        gmbc.benchLiveVariables();
        gmbc.benchReachingDefinitions();
    }
}
