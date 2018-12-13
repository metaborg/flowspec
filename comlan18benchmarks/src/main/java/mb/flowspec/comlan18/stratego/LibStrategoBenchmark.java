package mb.flowspec.comlan18.stratego;

public class LibStrategoBenchmark extends StrategoBenchmark {
    public LibStrategoBenchmark() {
        super(LibStrategoBenchmark.class.getResource("/libstratego-lib.ctree"));
    }
}
