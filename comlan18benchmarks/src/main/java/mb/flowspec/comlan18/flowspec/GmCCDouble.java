package mb.flowspec.comlan18.flowspec;

/**
 * Benchmarking data-flow analysis implementations in FlowSpec for Green-Marl (Gm) on the
 * Closeness Centrality Double (CCDouble) program.
 */
public class GmCCDouble extends FlowSpecGmBenchmark {
    public GmCCDouble() {
        super("/closeness_centrality_double.aterm");
    }
}
