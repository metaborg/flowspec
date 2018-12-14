package mb.flowspec.comlan18.stratego;

import java.net.URL;

import org.metaborg.core.MetaborgException;
import org.openjdk.jmh.annotations.Benchmark;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.strc.mark_bound_unbound_vars_0_0;

import mb.flowspec.comlan18.BaseBenchmark;

public abstract class StrategoBenchmark extends BaseBenchmark {
    protected StrategoBenchmark(URL inputURL) {
        super(inputURL);
    }

    @Benchmark
    public IStrategoTerm bench() throws MetaborgException, InterruptedException {
        return mark_bound_unbound_vars_0_0.instance.invoke(new Context(), ctree);
    }
}
