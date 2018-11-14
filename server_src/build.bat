del data\server.jar
cd src
javac -encoding utf-8 -cp ../lib/*.jar online/*.java -d ../data/
cd ..
jar -cvfm data/server.jar MANIFEST.MF -C data/ .
rd /s/q data\online
java -jar data\server.jar
