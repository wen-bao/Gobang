cd src
javac -encoding utf-8 net/wenbaobao/*.java -d ../bin/
javac -encoding utf-8 ai/*.java -d ../bin/
javac -encoding utf-8 socket/*.java -d ../bin/
cd ..
jar -cvfm FiveChess.jar MANIFEST.MF -C bin/ .
rd /s/q bin\ai bin\socket bin\net