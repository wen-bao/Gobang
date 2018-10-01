package net.wenbaobao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.UnknownHostException;
import java.util.LinkedList;


@SuppressWarnings("serial")
public class FiveChess extends JFrame implements ActionListener {
    FiveBoard board 		= null;
    Online online 			= null;
    Demon demon 			= null;
    MakeChessManual record 	= null;
    Container con 			= null;

    JMenuBar bar;
    JMenu fileMenu1, fileMenu2;
    JMenuItem 新游戏, 制作棋谱, 保存棋谱, 演示棋谱;
    JMenuItem 双人, 联机;
    JMenu 人机;
    JMenuItem 初级, 中级, 高级;
    JFileChooser fileChooser = null;

    LinkedList<Point> ChessManual 	= null;

    Dimension   screensize   =   Toolkit.getDefaultToolkit().getScreenSize();

    boolean com = true;
    int lev = 1;

    public FiveChess() {

        bar = new JMenuBar();

        fileMenu1 = new JMenu("五子棋");

        新游戏  	= new JMenuItem("新游戏");
        新游戏.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        制作棋谱 = new JMenuItem("制作棋谱");
        保存棋谱 = new JMenuItem("保存棋谱");
        演示棋谱 = new JMenuItem("演示棋谱");

        保存棋谱.setEnabled(false);

        fileMenu1.add(新游戏);
        fileMenu1.add(制作棋谱);
        fileMenu1.add(保存棋谱);
        fileMenu1.add(演示棋谱);

        bar.add(fileMenu1);

        新游戏.addActionListener(this);
        制作棋谱.addActionListener(this);
        保存棋谱.addActionListener(this);
        演示棋谱.addActionListener(this);


        fileMenu2 = new JMenu("模式");

        双人 = new JMenuItem("双人");

        人机 = new JMenu("人机");
        初级 = new JMenuItem("初级");
        中级 = new JMenuItem("中级");
        高级 = new JMenuItem("高级");
        人机.add(初级);
        人机.add(中级);
        人机.add(高级);

        联机 = new JMenuItem("联机");
        //联机.setEnabled(false);

        fileMenu2.add(双人);
        fileMenu2.add(人机);
        fileMenu2.add(联机);

        bar.add(fileMenu2);
        双人.addActionListener(this);
        联机.addActionListener(this);
        初级.addActionListener(this);
        中级.addActionListener(this);
        高级.addActionListener(this);


        setJMenuBar(bar);
        setTitle(制作棋谱.getText());

        board 	= new FiveBoard(35, 35, 15, 15);
        record 	= board.record;
        con 	= getContentPane();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, board, record);
        split.setEnabled(false);

        split.setDividerSize(5);

        split.setDividerLocation(550);

        con.add(split, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setVisible(true);

        setBounds((int)(screensize.getWidth() - 850) / 2, (int)(screensize.getHeight() - 600) / 2, 860, 600);
        setResizable(false);
        fileChooser = new JFileChooser();
        con.validate();
        validate();

    }

    private void reset() {
        con.removeAll();

        board 	= new FiveBoard(35, 35, 15, 15);
        record 	= board.record;

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, board, record);

        split.setDividerSize(5);
        split.setDividerLocation(550);
        split.setEnabled(false);

        con.add(split, BorderLayout.CENTER);
        validate();
    }

    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == 新游戏) {

            reset();
            board.computer = com;
            board.ai.level = lev;
        }

        if (e.getSource() == 制作棋谱) {
            reset();
            board.computer = com;
            board.ai.level = lev;

            保存棋谱.setEnabled(true);
            this.setTitle(制作棋谱.getText());

        }

        if (e.getSource() == 保存棋谱) {
            int state = fileChooser.showSaveDialog(null);
            File saveFile = fileChooser.getSelectedFile();

            if (saveFile != null && state == JFileChooser.APPROVE_OPTION) {
                try {
                    FileOutputStream outOne = new FileOutputStream(saveFile);
                    ObjectOutputStream outTwo = new ObjectOutputStream(outOne);
                    outTwo.writeObject(record.getChessManual());
                    outOne.close();
                    outTwo.close();
                } catch (IOException event) {
                }
            }
        }

        if (e.getSource() == 演示棋谱) {
            con.removeAll();
            con.repaint();
            con.validate();
            validate();
            保存棋谱.setEnabled(false);

            int state = fileChooser.showOpenDialog(null);
            File openFile = fileChooser.getSelectedFile();

            if (openFile != null && state == JFileChooser.APPROVE_OPTION) {
                try {
                    FileInputStream inOne = new FileInputStream(openFile);
                    ObjectInputStream inTwo = new ObjectInputStream(inOne);
                    ChessManual = (LinkedList<Point>) inTwo.readObject();

                    inOne.close();
                    inTwo.close();
                    FiveBoard board = new FiveBoard(35, 35, 15, 15);
                    demon = new Demon(board);
                    demon.setChessManual(ChessManual);
                    con.add(demon, BorderLayout.CENTER);
                    con.validate();
                    validate();
                    this.setTitle(演示棋谱.getText() + ":" + openFile);
                } catch (Exception event) {
                    JLabel label = new JLabel("不是棋谱文件");
                    label.setFont(new Font("隶书", Font.BOLD, 60));
                    label.setForeground(Color.red);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    con.add(label, BorderLayout.CENTER);
                    con.validate();
                    this.setTitle("没有打开棋谱");
                    validate();
                }
            } else {
                JLabel label = new JLabel("没有打开棋谱文件呢");
                label.setFont(new Font("隶书", Font.BOLD, 50));
                label.setForeground(Color.pink);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                con.add(label, BorderLayout.CENTER);
                con.validate();
                this.setTitle("没有打开棋谱文件呢");
                validate();
            }
        }

        if(e.getSource() == 联机) {
            con.removeAll();

            board = new FiveBoard(35, 35, 15, 15);
            record = board.record;
            try {
                online = new Online(board, record);
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, board, record);
            split.setDividerSize(5);
            split.setDividerLocation(550);
            split.setEnabled(false);

            con.add(split, BorderLayout.CENTER);
            con.validate();
            validate();
            record.something.setText("联机模式");
            record.chat.setText("");
            this.setTitle("联机比赛中...");
            board.lianji 	= true;
            board.computer 	= false;
            board.shuangren = false;

            board.lianjichat = "";
            board.lianjiX = -1;
            board.lianjiY = -1;
        }


        if(e.getSource() == 双人) {
            reset();
            record.something.setText("双人模式");
            board.shuangren = true;
            record.chat.setText("");
            com = false;
            board.lianji 	= false;
            board.computer 	= false;
            board.shuangren = true;
        }

        if(e.getSource() == 初级) {
            reset();
            record.something.setText("人机模式  黑棋");
            record.chat.append("AI > 你好，请多指教！\n");
            com = true;
            board.lianji 	= false;
            board.computer 	= true;
            board.shuangren = false;
            lev = 1;
            board.ai.level = 1;
        }

        if(e.getSource() == 中级) {
            reset();
            record.something.setText("人机模式  黑棋");
            record.chat.append("AI > 你好，请多指教！\n");
            com = true;
            board.lianji 	= false;
            board.computer 	= true;
            board.shuangren = false;
            lev = 2;
            board.ai.level = 2;
        }

        if(e.getSource() == 高级) {
            reset();
            record.something.setText("人机模式  黑棋");
            record.chat.append("AI > 你好，请多指教！\n");
            com = true;
            board.lianji 	= false;
            board.computer 	= true;
            board.shuangren = false;
            lev = 3;
            board.ai.level = 3;
        }


    }


    public static void main(String args[]) {
        new FiveChess();

        Music music = new Music();
        music.play();

    }
}