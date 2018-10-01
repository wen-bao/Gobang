cd src
pwd
echo "building =====>"
$(javac net/wenbaobao/*.java -d ../bin/)
$(javac ai/*.java -d ../bin/)
$(javac socket/*.java -d ../bin/)
echo "running ====>"
cd ../bin
$(java net.wenbaobao.FiveChess)
