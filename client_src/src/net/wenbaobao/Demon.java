package net.wenbaobao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

@SuppressWarnings("serial")
public class Demon extends JPanel implements ActionListener, Runnable {

    public JButton replay 	= null;
    public JButton next 	= null;
    public JButton auto 	= null;
    public JButton stop 	= null;

    JSplitPane splitH 	= null;
    JSplitPane splitV 	= null;
    JTextArea text		= null;
    JTextField 时间间隔 	= null;

    FiveBoard board = null;
    Rule rule 		= null;
    FiveChess fc    = null;

    LinkedList<Point> ChessManual = null;

    Thread 自动演示 = null;

    int index		= -1;
    double time 	= 1000;
    String 演示过程 	= "";



    public Demon(FiveBoard board, FiveChess fcc) {
        this.board 		= board;
        this.fc         = fcc;
        board.DemonRun 	= true;

        rule 	= new Rule(board, board.point);
        replay 	= new JButton("重新演示");
        next 	= new JButton("下一步");
        auto 	= new JButton("自动演示");
        stop 	= new JButton("暂停演示");
        自动演示 	= new Thread(this);

        replay.addActionListener(this);
        next.addActionListener(this);
        auto.addActionListener(this);
        stop.addActionListener(this);

        text 	= new JTextArea();
        text.setEditable(false);
        text.setBackground(new Color(248, 207, 117));
        时间间隔	= new JTextField("1");

        JLabel jLab = new JLabel("时间间隔(秒)", SwingConstants.CENTER);
        setLayout(new BorderLayout());
        JScrollPane pane = new JScrollPane(text);
        JPanel p = new JPanel(new GridLayout(3, 2));

        p.add(next);
        p.add(replay);
        p.add(auto);
        p.add(stop);
        p.add(jLab);
        p.add(时间间隔);

        splitV = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, p);
        splitH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board, splitV);
        splitV.setDividerSize(5);
        splitV.setDividerLocation(440);
        splitV.setEnabled(false);
        splitH.setDividerSize(5);
        splitH.setDividerLocation(550);
        splitH.setEnabled(false);
        add(splitH, BorderLayout.CENTER);
        validate();
    }

    public void setChessManual(LinkedList<Point> ChessManual) {
        this.ChessManual = ChessManual;
    }

    public char numberToLetter(int n) {
        int c = 'A';

        return (char) (c + n - 1);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == next) {
            index++;

            if (index < ChessManual.size()) {
                oneStep(index);
            } else {
                runOver("棋谱演示完毕");
            }

        }

        if (e.getSource() == replay) {
            board = new FiveBoard(35, 35, 15, 15, fc);
            board.DemonRun = true;
            board.computer = false;
            rule = new Rule(board, board.point);

            splitH.remove(board);
            splitH.setDividerSize(5);
            splitH.setDividerLocation(550);
            splitH.setLeftComponent(board);
            splitH.validate();

            index = -1;
            text.setText(null);
        }

        if (e.getSource() == auto) {
            next.setEnabled(false);
            replay.setEnabled(false);

            try {
                time = 1000 * Double.parseDouble(时间间隔.getText().trim());
            } catch (NumberFormatException ee) {
                time = 1000;
            }

            if (!(自动演示.isAlive())) {
                自动演示 	= new Thread(this);
                board 	= new FiveBoard(35, 35, 15, 15, fc);

                board.DemonRun = true;
                board.computer = false;
                rule = new Rule(board, board.point);

                splitH.remove(board);
                splitH.setDividerSize(5);
                splitH.setDividerLocation(550);
                splitH.setLeftComponent(board);
                splitH.validate();
                text.setText(null);
                自动演示.start();
            }

        }
        if (e.getSource() == stop) {

            if (e.getActionCommand().equals("暂停演示")) {
                演示过程 = "暂停演示";
                stop.setText("继续演示");
                stop.repaint();
            }

            if (e.getActionCommand().equals("继续演示")) {
                演示过程 = "继续演示";
                自动演示.interrupt();
                stop.setText("暂停演示");
                stop.repaint();
            }

        }
    }

    public synchronized void run() {

        for (index = 0; index < ChessManual.size(); index++) {

            try {
                try {
                    time = 1000 * Double.parseDouble(时间间隔.getText().trim());
                } catch (NumberFormatException ee) {
                    time = 1000;
                }
                Thread.sleep((int)time);
            } catch (InterruptedException e) {
            }

            while (演示过程.equals("暂停演示")) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    notifyAll();
                }
            }
            oneStep(index);
        }

        if (index >= ChessManual.size()) {
            runOver("棋谱演示完毕");
            next.setEnabled(true);
            replay.setEnabled(true);
        }

    }

    public void oneStep(int index) {

        Point step = (Point) ChessManual.get(index);
        int clickX = step.x;
        int clickY = step.y;
        FivePiece piece;

        if (index % 2 == 1) {
            piece = new FivePiece(1, Color.white, board.unitWidth - 4, board.unitHeight - 4, board, board.step, Color.black);
            board.step ++;
            piece.setColorType(board.whiteColor);
            board.whiteRun = false;
            board.blackRun = true;
        } else {
            piece = new FivePiece(1, Color.black, board.unitWidth - 4, board.unitHeight - 4, board, board.step, Color.white);
            board.step ++;
            piece.colortype = board.blackColor;
            board.blackRun = false;
            board.whiteRun = true;
        }

        (board.point)[clickX][clickY].setPiece(piece, board);

        String 棋子类别 = piece.colorType();
        String m = "#" + 棋子类别 + ": " + clickX + numberToLetter(clickY) + "\n";
        text.append(m);

        rule.movePieceRule(piece, clickX, clickY);
        rule.isWine(piece);

    }

    public void runOver(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}