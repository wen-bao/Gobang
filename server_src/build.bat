del data\server.jar
cd src
javac -encoding utf-8 socket/*.java -d ../data/
cd ..
jar -cvfm data/server.jar MANIFEST.MF -C data/ .
rd /s/q data\socket
java -jar data\server.jar
