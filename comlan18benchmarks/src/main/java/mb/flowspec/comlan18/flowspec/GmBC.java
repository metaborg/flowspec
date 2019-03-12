package mb.flowspec.comlan18.flowspec;

/**
 * Benchmarking data-flow analysis implementations in FlowSpec for Green-Marl (Gm) on the
 * Betweenness Centrality (BC) program.
 */
public class GmBC extends FlowSpecGmBenchmark {
    public GmBC() {
        super("/bc.aterm");
    }
}
