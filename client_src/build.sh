cd src
javac -encoding utf-8 net/wenbaobao/*.java -d ../data/
javac -encoding utf-8 ai/*.java -d ../data/
cd ..
jar -cvfm FiveChess.jar MANIFEST.MF -C data/ .
rm -rf data/ai data/net
