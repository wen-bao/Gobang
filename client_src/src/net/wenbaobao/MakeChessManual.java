package net.wenbaobao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class MakeChessManual extends JPanel implements ActionListener {

    JPanel state;
    public Label something = null, who = null;

    JSplitPane splitV;

    public JTextArea chat = null;
    JScrollPane scroll = null;

    JPanel text_but = null;
    JPanel sent_input = null;
    JTextField input = null;
    JButton sent = null;
    JButton buttonUndo = null;

    FiveBoard board = null;
    JTextArea text = null;
    FivePoint[][] point;
    LinkedList<Point> ChessManual = null;

    public MakeChessManual(FiveBoard board, FivePoint[][] point) {
        this.board = board;
        this.point = point;
        ChessManual = new LinkedList<Point>();

        text = new JTextArea();

        state = new JPanel();
        state.setLayout(new GridLayout(2, 0));
        something = new Label();
        something.setText("黑棋");
        who = new Label();
        who.setText("黑方执棋");
        state.add(something);
        state.add(who);

        chat = new JTextArea();
        chat.setBackground(new Color(248, 207, 117));
        chat.setEditable(false);
        scroll = new JScrollPane(chat);

        input = new JTextField(19);
        // input.addActionListener(this);
        sent = new JButton("发送");
        sent.addActionListener(this);
        sent.addKeyListener(key_Listener);
        sent_input = new JPanel();
        sent_input.setLayout(new FlowLayout());
        sent_input.add(input);
        sent_input.add(sent);
        buttonUndo = new JButton("悔棋");
        buttonUndo.setFont(new Font("隶书", Font.PLAIN, 18));
        buttonUndo.addActionListener(this);
        text_but = new JPanel();
        text_but.setLayout(new GridLayout(2, 0));
        text_but.add(sent_input);
        text_but.add(buttonUndo);

        splitV = new JSplitPane(JSplitPane.VERTICAL_SPLIT, state, scroll);
        splitV.setDividerSize(20);
        splitV.setDividerLocation(60);
        splitV.setEnabled(false);

        setLayout(new BorderLayout());
        add(splitV, BorderLayout.CENTER);
        add(text_but, BorderLayout.SOUTH);

    }

    public char numberToLetter(int n) {
        int num = 'A' + n - 1;
        return (char) num;
    }

    public void saveChessManual(FivePiece piece, int clickX, int clickY) {

        Point click = new Point(clickX, clickY);
        ChessManual.add(click);

        String 棋子类别 = piece.colorType();
        String m = "#" + 棋子类别 + ": " + clickX + numberToLetter(clickY) + "\n";
        text.append(m);

    }

    public LinkedList<Point> getChessManual() {
        return ChessManual;
    }

    private void sentinfo() {
        int len = input.getText().length();
        if (len != 0 && len <= 20) {
            // System.out.println(input.getText());
            String whosay;
            if (board.computer) {
                whosay = "人类 > ";
                chat.append(whosay + input.getText() + "\n");
                chat.setCaretPosition(chat.getDocument().getLength());
                input.setText("");
                input.requestFocus();
                chat.append(board.ai.say());
                chat.setCaretPosition(chat.getDocument().getLength());
            } else if(board.shuangren) {
                whosay = " > ";
                chat.append(whosay + input.getText() + "\n");
                chat.setCaretPosition(chat.getDocument().getLength());
                input.setText("");
                input.requestFocus();
            } else if(board.lianji){
                if(board.strPersonColor == "black") {
                    whosay = "黑棋 > ";
                } else {
                    whosay = "白棋 > ";
                }
                String str = whosay + input.getText() + "\n";
                chat.append(str);
                board.online.writer.println(board.OnlineId + ":1:" + str);
                board.online.writer.flush();
                input.setText("");
                input.requestFocus();
            }else {
                if(board.strPersonColor == "black") {
                    whosay = "白棋 > ";
                } else {
                    whosay = "黑棋 > ";
                }
                String str = whosay + input.getText() + "\n";
                chat.append(str);
                chat.setCaretPosition(chat.getDocument().getLength());
                input.setText("");
                input.requestFocus();
            }
        }
        if (len > 20) {
            JOptionPane.showMessageDialog(null, "输入长度不超过20");
        }
    }

    
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == buttonUndo) {
            solveUndo();
        }

        if (e.getSource() == sent) {
            sentinfo();
        }
    }

    @SuppressWarnings("deprecation")
    public void solveUndo() {
        int position = text.getText().lastIndexOf("#");

        if (position != -1) {
            text.replaceRange("", position, text.getText().length());
        }

        if (ChessManual.size() > 0) {

            Point lastStep = (Point) ChessManual.getLast();
            ChessManual.removeLast();

            int clickX = lastStep.x;
            int clickY = lastStep.y;

            board.ai.remove(clickX, clickY);
            board.step--;

            // System.out.println(clickX+"_"+clickY);
            FivePiece piece = point[clickX][clickY].getPiece();
            board.hadWin = false;
            if (piece.colorType().equals(board.whiteColor)) {

                piece.hide();

                piece = new FivePiece(0, null, board.unitWidth - 4, board.unitHeight - 4, board, 0, Color.black);
                point[clickX][clickY].setPiece(piece, board);
                board.whiteRun = true;
                board.blackRun = false;
            } else if (piece.colorType().equals(board.blackColor)) {
                piece.hide();
                piece = new FivePiece(0, null, board.unitWidth - 4, board.unitHeight - 4, board, 0, Color.black);
                point[clickX][clickY].setPiece(piece, board);
                board.blackRun = true;
                board.whiteRun = false;
            }
        }
    }

    KeyListener key_Listener = new KeyListener() {
        public void keyTyped(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {
            if(e.getKeyChar() == KeyEvent.VK_ENTER ) {
                sentinfo();
            }
        }
    };
}