
mvn clean install

mkdir -p results/flowspec
mkdir -p results/stratego

java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libspoofax.csv ".*flowspec\.LibSpoofaxBenchmark"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libstratego.csv ".*flowspec\.LibStrategoBenchmark"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libstrc.csv ".*flowspec\.LibStrcBenchmark"

java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libspoofax.csv ".*stratego\.LibSpoofaxBenchmark"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libstratego.csv ".*stratego\.LibStrategoBenchmark"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libstrc.csv ".*stratego\.LibStrcBenchmark"
