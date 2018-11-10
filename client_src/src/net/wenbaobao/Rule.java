package net.wenbaobao;

import javax.swing.*;


public class Rule {

    FiveBoard board = null;
    FivePiece piece = null;
    FivePoint point[][];

    int clickX, clickY;

    public Rule(FiveBoard board, FivePoint point[][]) {
        this.board = board;
        this.point = point;
    }

    public boolean isWine(FivePiece piece) {

        this.piece = piece;

        boolean win = false;
        String colorT = piece.colorType();
        int R = board.xLong, D = board.yLong;

        if(colorT == null) return false;

        //System.out.println(colortT);

        int u = numInLine(0, -1, 1, R, D, colorT);
        int d = numInLine(0,  1, 1, R, D, colorT);

        int l = numInLine(-1, 0, 1, R, D, colorT);
        int r = numInLine( 1, 0, 1, R, D, colorT);

        int ul = numInLine(-1, -1, 1, R, D, colorT);
        int dr = numInLine( 1,  1, 1, R, D, colorT);

        int ur = numInLine( 1, -1, 1, R, D, colorT);
        int dl = numInLine(-1,  1, 1, R, D, colorT);

        //System.out.println(clickX+"_"+clickY);
        //System.out.println(u+"_"+d+" "+l+"_"+r+" "+ul+"_"+dr+" "+ur+"_"+dl);

        if(u + d >= 6 || l + r >= 6 || ul + dr >= 6 || ur + dl >= 6) win = true;

        if(win && board.computer) {
            board.whoWin = colorT;
            if(colorT == "black") {
                JOptionPane.showMessageDialog(null, "恭喜击败傻逼AI!");
            } else {
                JOptionPane.showMessageDialog(null, "很遗憾被傻逼AI击败!");
            }
        } else if(win && board.lianji) {
            board.whoWin = colorT;
            if(colorT == board.strPersonColor) {
                JOptionPane.showMessageDialog(null, "恭喜击败对方!");
            } else {
                JOptionPane.showMessageDialog(null, "很遗憾被对方击败!");
                Object[] options ={ "复仇", "换人" };  //自定义按钮上的文字
                int m = JOptionPane.showOptionDialog(null, "是否复仇？", "To be or not to be",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if(m == 0) {
                    //System.out.println("复仇");
                    board.online.writer.println(board.online.id + ":0:0");
                    board.online.writer.flush();
                } else {
                    board.online.writer.println("=_=");
                    board.online.writer.flush();
                    //System.out.println("换人");
                }
            }
        } else if(win) {
            board.whoWin = colorT;
            JOptionPane.showMessageDialog(null, colorT + "胜利！");
        }

        return win;
    }

    public int numInLine(int xMove, int yMove, int min, int maxX, int maxY, String colorT) {
        int numLine = 0;

        while(clickX + numLine * xMove >= min && clickX + numLine * xMove <= maxX &&
                clickY + numLine * yMove >= min && clickY + numLine * yMove <= maxY &&
                pointColorType(clickX + numLine * xMove, clickY + numLine * yMove) == colorT) {

            numLine ++;
        }

        return numLine;
    }

    public boolean movePieceRule(FivePiece piece, int clickX, int clickY) {

        this.piece 	= piece;
        this.clickX = clickX;
        this.clickY = clickY;

        return point[clickX][clickY].getPiece().name == 0;
    }

    public String pointColorType(int x, int y) {
        return point[x][y].getPiece().colorType();
    }

}