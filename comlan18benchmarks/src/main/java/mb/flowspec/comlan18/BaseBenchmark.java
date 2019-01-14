package mb.flowspec.comlan18;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(jvmArgsAppend = {"-Xms512m", "-Xmx1024m", "-Xss16m"})
public abstract class BaseBenchmark {
    final protected URL inputURL;
    protected IStrategoTerm ctree;

    protected BaseBenchmark(URL inputURL) {
        this.inputURL = inputURL;
    }

    @Setup
    public void setupInput() throws IOException { 
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
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
