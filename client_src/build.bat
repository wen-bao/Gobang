cd src
javac -encoding utf-8 net/wenbaobao/*.java -d ../data/
javac -encoding utf-8 ai/*.java -d ../data/
javac -encoding utf-8 socket/*.java -d ../data/
cd ..
jar -cvfm FiveChess.jar MANIFEST.MF -C data/ .
rd /s/q data\ai data\socket data\net
