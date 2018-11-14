rm -rf data/server.jar
cd src
javac -encoding utf-8 -cp ../lib/*.jar online/*.java -d ../data/
cd ..
jar -cvfm data/server.jar MANIFEST.MF -C data/ .
rm -rf data/online
gcc -w src/run.c -o run
