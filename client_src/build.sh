cd src
javac -encoding net/wenbaobao/*.java -d ../bin/
javac -encoding ai/*.java -d ../bin/
javac -encoding socket/*.java -d ../bin/
cd ..
jar -cvfm FiveChess.jar MANIFEST.MF -C bin/ .
rm -rf bin/ai bin/socket bin/net