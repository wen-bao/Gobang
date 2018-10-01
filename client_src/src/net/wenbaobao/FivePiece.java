package net.wenbaobao;

import javax.swing.*;

import java.awt.*;

@SuppressWarnings("serial")
public class FivePiece extends JLabel {

    int name, step;
    int width, height;
    Color backcolor, fontcolor;
    String colortype = null;// colorWhite colorBlack

    FiveBoard board  = null;

    public FivePiece(int name, Color bc, int width, int height, FiveBoard board, int step, Color fontcolor) {

        this.name 		= name;
        this.backcolor 	= bc;
        this.width 		= width;
        this.height 	= height;
        this.board 		= board;
        this.step 		= step;
        this.fontcolor 	= fontcolor;

        setSize(width, height);
        setBackground(bc);

        addMouseMotionListener(board);
        addMouseListener(board);

    }

    public void paint(Graphics g) {
        g.drawImage(board.pieceImage, 2, 2, width - 1, height - 1, null);

        if (this.name == 1) {
            g.setColor(backcolor);
            g.fillOval(2, 2, width - 2, height - 2);

            g.setColor(fontcolor);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            if(step <= 9) {
                g.drawString(step + "", 13, 22);
            } else if (step <= 99) {
                g.drawString(step + "", 5, 20);
            } else {
                g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
                g.drawString(step + "", 5, 19);
            }
        }

    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getNames() {
        return this.name;
    }

    public Color getColor() {
        return this.backcolor;
    }

    public String colorType() {
        return this.colortype;
    }

    public void setColorType(String cl) {
        this.colortype = cl;
    }

}