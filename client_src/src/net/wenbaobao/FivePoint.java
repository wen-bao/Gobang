package net.wenbaobao;

public class FivePoint {

    int x, y;

    boolean havePiece;

    FivePiece piece = null;

    FiveBoard board = null;


    public FivePoint(int x, int y, boolean bool) {
        this.x 		= x;
        this.y 		= y;
        havePiece 	= bool;
    }

    public boolean isFivePiece() {
        return havePiece;
    }

    public void setHavePiece(boolean bool) {
        this.havePiece = bool;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public FivePiece getPiece() {
        return piece;
    }

    // 设置改点棋子
    public void setPiece(FivePiece piece, FiveBoard board) {

        this.board = board;
        this.piece = piece;

        board.add(piece);

        int w = (board.unitWidth);
        int h = (board.unitHeight);

        piece.setBounds(x - w / 2, y - h / 2, w, h);// 棋子位置，宽度，高度

        havePiece = true;
        board.validate();

    }

    public void reMovePiece(FivePiece piece, FiveBoard board) {

        this.board = board;
        this.piece = piece;

        board.remove(piece);
        board.validate();

        havePiece = false;
    }

}