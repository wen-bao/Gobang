package ai;

import java.awt.Point;
import java.util.Random;


public class AI {

    int size  = 15;
    int step  = 0;
    int Empty = 3;
    int Black = 1;
    int White = 2;

    int oneDead = 1;
    int twoDead = 2;
    int threeDead = 3;
    int fourDead = 4;
    int fiveDead = 5;

    int oneBlock = 6;
    int twoBlock = 7;
    int threeBlock = 8;
    int fourBlock = 9;
    int fiveBlock = 10;

    int oneLive = 11;
    int twoLive = 12;
    int threeLive = 13;
    int fourLive = 14;
    int fiveLive = 15;

    public MyCell mycell[][];
    public int level = 1;


    public AI(int size) {

        this.size = size;

        mycell = new MyCell[size + 1][size + 1];

        for(int i = 1; i <= size; ++i) {
            for(int j = 1; j <= size; ++j) {
                mycell[i][j] = new MyCell(Empty);
            }
        }

    }

    public String say() {
        Random random = new Random();
        //System.out.println(word.length);
        int len = word.length;
        return "AI > " + word[(random.nextInt() % len + len) % len] + "\n";
    }

    public void PutChess(Point input, int colorT) {
        step ++;
        mycell[input.x][input.y].pieceType = colorT;
    }

    public void remove(int x, int y) {
        step --;
        mycell[x][y].pieceType = Empty;
    }

    public Point GetBestMove() {

        Point aimPoint = new Point();

        if(step == 1) {

            if(mycell[size / 2 + 1][size / 2 + 1].pieceType == Empty) {
                aimPoint.x = size / 2 + 1;
                aimPoint.y = size / 2 + 1;
            } else {
                aimPoint.x = size / 2;
                aimPoint.y = size / 2;
            }

            PutChess(aimPoint, White);

            return aimPoint;
        }

        aimPoint = serach();

        System.out.println(aimPoint.x + " " + aimPoint.y);

        PutChess(aimPoint, White);

        return aimPoint;
    }


    Point serach() {

        Point best = new Point();

        if(level == 1) return oneLevel().point;
        else {
            doSomeThing(level, 1, 1, 0, 0, White);
        }
        return best;
    }

    private Pos oneLevel() {

        Pos aim = new Pos(new Point(1, 1), 0);
        for(int i = 1; i <= size; ++i) {
            for(int j = 1; j <= size; ++j) {
                if(mycell[i][j].pieceType == Empty) {

                    mycell[i][j].pieceType = Black;
                    int tryBlack = evelution(i, j, Black);
                    if(tryBlack > aim.val) {
                        aim.val = tryBlack;
                        aim.point.x = i;
                        aim.point.y = j;
                    }
                    mycell[i][j].pieceType = Empty;

                    mycell[i][j].pieceType = White;
                    int tryWhite = evelution(i, j, White);
                    if(tryWhite >= aim.val) {
                        aim.val = tryWhite;
                        aim.point.x = i;
                        aim.point.y = j;

                    }
                    mycell[i][j].pieceType = Empty;
                }
            }
        }



        return aim;

    }

    private int doSomeThing(int level, int x, int y, int alph, int beta, int colorT) {

        if(level <= 0) return oneLevel().val;

        mycell[x][y].pieceType = colorT;

        int minmax = 0;

        for(int i = 1; i <= size; ++i) {
            for(int j = 1; j <= size; ++j) {
                if(mycell[i][j].pieceType == Empty) {
                    int tmp = doSomeThing(level - 1, i, j, alph, beta, 1 - colorT);
                    if(colorT == Black) {
                        if(tmp < minmax) minmax = tmp;
                    } else {
                        if(tmp > minmax) minmax = tmp;
                    }
                }
            }
        }

        mycell[x][y].pieceType = Empty;

        return minmax;
    }

    private int evelution(int x, int y, int piece) {

        int colorT = piece;

        int sco[] = new int[16];

        LineInfo u = numOfLine(x, y, 0, -1, 1, size, colorT);
        LineInfo d = numOfLine(x, y, 0,  1, 1, size, colorT);
        sco[Scone(u, d)] ++;

        LineInfo l = numOfLine(x, y, -1, 0, 1, size, colorT);
        LineInfo r = numOfLine(x, y,  1, 0, 1, size, colorT);
        sco[Scone(l, r)] ++;

        LineInfo ul = numOfLine(x, y, -1, -1, 1, size, colorT);
        LineInfo dr = numOfLine(x, y,  1,  1, 1, size, colorT);
        sco[Scone(ul, dr)] ++;

        LineInfo ur = numOfLine(x, y, 1, -1, 1, size, colorT);
        LineInfo dl = numOfLine(x, y, -1, 1, 1, size, colorT);
        sco[Scone(ur, dl)]++;

        if(sco[fiveDead] >= 1 || sco[fiveBlock] >= 1 || sco[fiveLive] >= 1) return 100;
        else if(sco[fourLive] >= 1 || sco[fourBlock] >= 2 || (sco[fourBlock] >= 1 && sco[threeLive] >= 1)) return 90;
        else if(sco[threeLive] >= 2) return 80;
        else if(sco[threeBlock] >= 1 && sco[threeLive] >= 1) return 70;
        else if(sco[fourBlock] >= 1) return 60;
        else if(sco[threeLive] >= 1) return 50;
        else if(sco[twoLive] >= 2) return 40;
        else if(sco[threeBlock] >= 1) return 30;
        else if(sco[twoLive] >= 1) return 20;
        else if(sco[twoBlock] >= 1) return 10;
        else return 0;
    }

    private LineInfo numOfLine(int x, int y, int xMove, int yMove, int min, int max, int colorT) {

        LineInfo lineinfo = new LineInfo(0, false);

        while(x + lineinfo.num * xMove >= min && x + lineinfo.num * xMove <= max &&
                y + lineinfo.num * yMove >= min && y + lineinfo.num * yMove <= max) {

            if(pointType(x + lineinfo.num * xMove, y + lineinfo.num * yMove) == colorT) {
                lineinfo.num ++;
            } else if (pointType(x + lineinfo.num * xMove, y + lineinfo.num * yMove) == Empty) {
                lineinfo.Live = true;
                break;
            } else {
                break;
            }
        }

        return lineinfo;
    }

    private int Scone(LineInfo x, LineInfo y) {
        //System.out.println("****" + (x.num + y.num - 1));
        if(x.Live && y.Live) {
            switch (x.num + y.num - 1) {
            case 1:
                return oneLive;
            case 2:
                return twoLive;
            case 3:
                return threeLive;
            case 4:
                return fourLive;
            case 5:
                return fiveLive;
            default:
                return 0;
            }
        } else if(x.Live || y.Live) {
            switch (x.num + y.num - 1) {
            case 1:
                return oneBlock;
            case 2:
                return twoBlock;
            case 3:
                return threeBlock;
            case 4:
                return fourBlock;
            case 5:
                return fiveBlock;
            default:
                return 0;
            }
        } else {
            switch (x.num + y.num - 1) {
            case 1:
                return oneDead;
            case 2:
                return twoDead;
            case 3:
                return threeDead;
            case 4:
                return fourDead;
            case 5:
                return fiveDead;
            default:
                return 0;
            }
        }
    }

    private int pointType(int x, int y) {
        return mycell[x][y].pieceType;
    }

    class LineInfo {
        int num;
        boolean Live;

        public LineInfo(int num, boolean Live) {
            this.num 	= num;
            this.Live	= Live;
        }
    }

    class MyCell {
        int pieceType;

        public MyCell(int pieceType) {
            this.pieceType = pieceType;
        }
    }

    class Pos {
        Point point;
        int val;

        public Pos(Point point, int val) {
            this.point 	= point;
            this.val 	= val;
        }
    }

    private String word[] = new String[] {
        "吃了吗？", "今天天气不错！", "有对象了吗？", "单身狗才会无聊和我下棋吧！", "感觉很厉害的样子",
        "快点下", "干得好", "游戏真棒", "你还在吗？", ":-)", "你真利害", "不用客气", "真走运", "祝你好运",
        "我在思考吃什么", "你是哪里人？", "你运气真好", "想我了吗？", "好像和你一直玩下去", "你不会是Gay吧",
        "好饿", "嘤嘤嘤", "奥，这样啊", "嘻嘻嘻", "你猜", "等会儿和我女朋友诳街"
    };
}