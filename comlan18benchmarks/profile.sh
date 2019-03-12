
mvn clean install

java -jar target/comlan18benchmarks.jar -f 1 -prof 'jmh.extras.JFR:dir=/tmp/profile-jfr;flameGraphDir=/Users/jeff/Git/FlameGraph;jfrFlameGraphDir=/Users/jeff/Git/jfr-flame-graph;flameGraphOpts=--minwidth,2;verbose=true'
