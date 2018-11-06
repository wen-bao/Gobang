cd src
javac net/wenbaobao/*.java -d ../bin/
javac ai/*.java -d ../bin/
javac socket/*.java -d ../bin/
cd ..
jar -cvfm FiveChess.jar MANIFEST.MF -C bin/ .
rm -rf bin/ai bin/socket bin/net

