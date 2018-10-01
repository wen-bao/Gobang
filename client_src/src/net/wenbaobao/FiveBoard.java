package net.wenbaobao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ai.*;

@SuppressWarnings("serial")
public class FiveBoard extends JPanel implements MouseListener, MouseMotionListener {

    public AI ai;
    public FivePoint point[][];
    public FivePiece piece[][];
    Rule rule = null;

    public int unitWidth, unitHeight;
    public int xLong, yLong;
    public String whiteColor = "white", blackColor = "black";
    int startX, startY, clickX, clickY;
    int step;

    private Image image;
    protected Image pieceImage;

    public boolean whiteRun = false;
    public boolean blackRun = true;
    public boolean hadWin 	= false;
    public String  whoWin   = "";

    public boolean DemonRun = false;

    public boolean shuangren = false;

    public boolean computer = false;
    public boolean thinking = false;

    public boolean lianji   = false;
    public boolean waitOther = false;
    public String strPersonColor = null;
    public String strOtherColor  = null;
    public Color personColor = null;
    public Color otherColor  = null;
    public String lianjichat = "";
    public int lianjiX = -1;
    public int lianjiY = -1;
    public int personX, personY;

    public MakeChessManual record = null;


    public FiveBoard(int w, int h, int r, int c) {

        this.unitWidth 	= w;
        this.unitHeight = h;
        this.xLong 		= r;
        this.yLong 		= c;

        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        setBackground(new Color(248, 207, 117));

        point = new FivePoint[r + 1][c + 1];
        step = 0;

        ai = new AI(r);
        computer = true;

        for(int i = 1; i <= r; ++i) {
            for(int j = 1; j <= c; ++j) {
                point[i][j] = new FivePoint(unitWidth * i, unitHeight * j, false);
            }
        }

        rule 	= new Rule(this, point);
        record 	= new MakeChessManual(this, point);
        record.something.setText("人机模式  黑棋");
        record.chat.append("AI > 你好，请多指教！\n");

        image 		= Toolkit.getDefaultToolkit().getImage("board.jpg");
        pieceImage 	= Toolkit.getDefaultToolkit().getImage("piece.gif");

        piece = new FivePiece[r + 1][c + 1];

        for(int i = 1; i <= r; ++i) {
            for(int j = 1; j <= c; ++j) {
                piece[i][j] = new FivePiece(0, null, w - 4, h - 4, this, 0, Color.black);
                point[i][j].setPiece(piece[i][j], this);
            }
        }

    }


    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        int imgWidth 	= image.getWidth(this);
        int imgHeight 	= image.getHeight(this);// 获得图片的宽度与高度
        int FWidth 		= getWidth();
        int FHeight 	= getHeight();// 获得窗口的宽度与高度

        int x = (FWidth - imgWidth) / 2;
        int y = (FHeight - imgHeight) / 2;

        g.drawImage(image, x, y, null);

        for (int j = 1; j <= yLong; ++j) {
            g.drawLine(point[1][j].x, point[1][j].y, point[xLong][j].x, point[xLong][j].y);
        }

        for(int i = 1; i <= xLong; ++i) {
            g.drawLine(point[i][1].x, point[i][1].y, point[i][yLong].x, point[i][yLong].y);
        }

        g.setFont(new Font("TimesRoman", Font.PLAIN, 15));

        for (int i = 1; i <= xLong; ++i) {
            g.drawString("" + i, i * unitWidth - 5, unitHeight / 2);
        }

        int j = 1;
        for (char c = 'A'; c < 'A' + yLong; ++c) {
            g.drawString("" + c, unitWidth / 4, j * unitHeight + 5);
            j++;
        }

        g.setColor(Color.black);
        g.fillOval(point[4][4 ].x - 5, point[4][4 ].y - 5, unitWidth - 20, unitHeight - 20);
        g.fillOval(point[4][8 ].x - 5, point[4][8 ].y - 5, unitWidth - 20, unitHeight - 20);
        g.fillOval(point[4][12].x - 5, point[4][12].y - 5, unitWidth - 20, unitHeight - 20);

        g.fillOval(point[8][4 ].x - 5, point[8][4 ].y - 5, unitWidth - 20, unitHeight - 20);
        g.fillOval(point[8][8 ].x - 5, point[8][8 ].y - 5, unitWidth - 20, unitHeight - 20);
        g.fillOval(point[8][12].x - 5, point[8][12].y - 5, unitWidth - 20, unitHeight - 20);

        g.fillOval(point[12][4 ].x - 5, point[12][4 ].y - 5, unitWidth - 20, unitHeight - 20);
        g.fillOval(point[12][8 ].x - 5, point[12][8 ].y - 5, unitWidth - 20, unitHeight - 20);
        g.fillOval(point[12][12].x - 5, point[12][12].y - 5, unitWidth - 20, unitHeight - 20);

        Graphics2D g2 = (Graphics2D)g;  //g是Graphics对象
        g2.setStroke(new BasicStroke(3.0f));
        g2.setColor(Color.black);
        g2.drawLine(point[1][1].x, point[1][1].y, point[1][yLong].x, point[1][yLong].y);
        g2.drawLine(point[1][1].x, point[1][1].y, point[xLong][1].x, point[xLong][1].y);
        g2.drawLine(point[xLong][1].x, point[xLong][1].y, point[xLong][yLong].x, point[xLong][yLong].y);
        g2.drawLine(point[1][yLong].x, point[1][yLong].y, point[xLong][yLong].x, point[xLong][yLong].y);

    }

    /**鼠标按下事件*/
    public void mouseClicked(MouseEvent e) {
        FivePiece piece = null;
        Rectangle rect 	= null;

        boolean containChessPoint = false;

        if (e.getSource() instanceof FivePiece) {

            piece 	= (FivePiece) e.getSource();
            startX 	= piece.getBounds().x;
            startY 	= piece.getBounds().y;
            rect 	= piece.getBounds();

            for (int i = 1; i <= xLong; i++) {
                for (int j = 1; j <= yLong; j++) {
                    int x = point[i][j].getX();
                    int y = point[i][j].getY();
                    if (rect.contains(x, y)) {
                        containChessPoint = true;
                        clickX = i;
                        clickY = j;
                        break;
                    }
                }
            }
            //System.out.println(startX+"."+startY+"_"+clickX+"."+clickY);
        }

        if(hadWin) {
            if(computer) {
                if(whoWin == "black") {
                    JOptionPane.showMessageDialog(null, "已经击败AI!");
                } else {
                    JOptionPane.showMessageDialog(null, "已经被AI击败!");
                }
            } else {
                JOptionPane.showMessageDialog(null, whoWin + "胜利！");
            }
        } else if(DemonRun) {
            JOptionPane.showMessageDialog(null, "正在演示棋谱");
        } else if (piece != null && containChessPoint) {
            boolean ok = rule.movePieceRule(piece, clickX, clickY);
            if (ok) {

                if(shuangren) {
                    solveShuangren();
                } else if (computer && !thinking) {
                    solveComputer(piece);
                } else if (lianji) {
                    solveLianji();
                }
            }

        }

    }

    private void solveLianji() {
        // TODO Auto-generated method stub

        if(waitOther) {
            JOptionPane.showMessageDialog(null, "正在等待对方落子");
            return;
        }
        setPiece(clickX, clickY, blackColor, personColor);
        lianjiX = clickX;
        lianjiY = clickY;
        step ++;
        waitOther = true;
    }


    private void solveComputer(FivePiece piece) {
        // TODO Auto-generated method stub
        if (thinking) {
            JOptionPane.showMessageDialog(null, "傻逼电脑正在思考...");
            return;
        }
        setPiece(clickX, clickY, blackColor, Color.black);
        step ++;
        blackRun = false;
        whiteRun = true;

        if(!hadWin) {

            thinking = true;

            Point input = new Point(clickX, clickY);
            ai.PutChess(input, 1);
            Point aim = ai.GetBestMove();
            rule.movePieceRule(piece, aim.x, aim.y);
            setPiece(aim.x, aim.y, whiteColor, Color.white);
            step ++;
            whiteRun = false;
            blackRun = true;

            thinking = false;
        }
    }


    private void solveShuangren() {
        // TODO Auto-generated method stub
        if (blackRun) {
            //System.out.println("white");
            setPiece(clickX, clickY, blackColor, Color.black);
            step ++;
            blackRun = false;
            whiteRun = true;
        } else {
            //System.out.println("black");
            setPiece(clickX, clickY, whiteColor, Color.white);
            step ++;
            whiteRun = false;
            blackRun = true;
        }
    }


    public void setPiece(int x, int y, String colorT, Color color) {

        Color fontcolor = null;

        if(color.black == color) {
            record.who.setText("白方执棋");
            fontcolor = color.white;
        } else {
            record.who.setText("黑方执棋");
            fontcolor = color.black;
        }

        FivePiece piece = new FivePiece(1, color, unitWidth - 4, unitHeight - 4, this, step, fontcolor);
        piece.setColorType(colorT);
        point[x][y].setPiece(piece, this);
        record.saveChessManual(piece, x, y);

        if(rule.isWine(piece)) {
            hadWin = true;
        } else {
            hadWin = false;
        }

    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}