
mvn clean install

mkdir -p results/flowspec
mkdir -p results/stratego

java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libspoofax.csv "FSLibSpoofaxBenchmark"
# java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libstratego.csv "FSLibStrategoBenchmark"
# java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libstrc.csv "FSLibStrcBenchmark"

java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libspoofax.csv "StrLibSpoofaxBenchmark"
# java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libstratego.csv "StrLibStrategoBenchmark"
# java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libstrc.csv "StrLibStrcBenchmark"
