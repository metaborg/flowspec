
# mvn clean install

mkdir -p results/flowspec
mkdir -p results/stratego

java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libspoofax.csv "FSLibSpoofaxBenchmark.bench2"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libstratego.csv "FSLibStrategoBenchmark.bench2"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/libstrc.csv "FSLibStrcBenchmark.bench2"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/flowspec/sepcomp.csv "FSSepCompBenchmark.bench2"

java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libspoofax.csv "StrLibSpoofaxBenchmark"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libstratego.csv "StrLibStrategoBenchmark"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/libstrc.csv "StrLibStrcBenchmark"
java -jar target/comlan18benchmarks.jar -f 1 -rff results/stratego/sepcomp.csv "StrSepCompBenchmark"
