package mb.flowspec.comlan18.flowspec;

/**
 * Benchmarking data-flow analysis implementations in FlowSpec for Green-Marl (Gm) on the
 * Closeness Centrality (CC) program.
 */
public class GmCC extends FlowSpecGmBenchmark {
    public GmCC() {
        super("/closeness_centrality.aterm");
    }
}
