package net.wenbaobao;

import java.awt.Color;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

public class Client {

    FiveBoard board = null;
    MakeChessManual record = null;
    Rule rule = null;
    JSplitPane split = null;

    BufferedReader reader;
    public PrintWriter writer;

    static String host = "47.95.120.196";
    static int port = 5555;

    public Client(FiveBoard board, MakeChessManual record) throws UnknownHostException, IOException {

        this.board = board;
        this.record = record;
        board.client = this;
        go();
    }

    public final String charset = "utf-8";

    public static String id;
    public Thread getThread;
    Socket socket;

    public void go() throws UnknownHostException, IOException {
        getServer();
        // 与服务端建立连接
        socket = new Socket(host, port);

        InputStreamReader streamReader = new InputStreamReader(socket.getInputStream(), charset);
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charset));

        // 接受消息的进程
        getThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    while (!socket.isClosed()) {
                        try {

                            if(!board.lianji || board.onlineEnd) {
                                socket.close();
                                break;
                            }

                            String info = reader.readLine();
                            if(info == null) continue;

                            String s[] = info.split(":");

                            //System.out.println(info);

                            if ("0".equals(s[0])) { // 来自系统
                                if ("1".equals(s[1])) { // 系统消息
                                    JOptionPane.showMessageDialog(null, s[2]);
                                } else if ("2".equals(s[1])) { // 分配棋子颜色
                                    board.onlineStart = true;
                                    if ("0".equals(s[2])) {
                                        board.personColor = Color.black;
                                        board.strPersonColor = "black";
                                        board.otherColor = Color.white;
                                        board.strOtherColor = "white";
                                        board.waitOther = false;
                                    } else {
                                        board.personColor = Color.white;
                                        board.strPersonColor = "white";
                                        board.otherColor = Color.black;
                                        board.strOtherColor = "black";
                                        board.waitOther = true;
                                    }
                                } else if("%".equals(s[1])){ // 换人
                                    //JOptionPane.showMessageDialog(null, "正在匹配更强的对手！");
                                    board.onlineAgain = true;
                                    board.fc.solveOnline();
                                    //board.onlineEnd = true;
                                } else { // 退出
                                    JOptionPane.showMessageDialog(null, "对手退出游戏");
                                    Object[] options ={ "重新匹配", "退出游戏" };
                                    int m = JOptionPane.showOptionDialog(null, "是否寻找新玩家？", "To be or not to be",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                    if(m == 0) { 
                                        //JOptionPane.showMessageDialog(null, "正在开始!");
                                        writer.println("$\n");
                                        writer.flush();
                                        board.onlineAgain = true;
                                        board.fc.solveOnline();
                                        board.onlineStart = false;
                                    } else {
                                        System.exit(0);
                                    }
                                }
                            } else { // 来自对手
                                if ("1".equals(s[1])) {
                                    record.chat.append(s[2]+"\n");
                                } else if ("0".equals(s[1])) { //复仇
                                    if("0".equals(s[2])) { //是否同意复仇
                                        Object[] options ={ "同意", "换人" };
                                        int m = JOptionPane.showOptionDialog(null, "对方前来复仇，是否同意？", "To be or not to be",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                        if(m == 0) { 
                                            JOptionPane.showMessageDialog(null, "正在开始！");
                                            writer.println("1:0:1\n");
                                            writer.flush();
                                        } else {
                                            writer.println("1:0:2\n");
                                            writer.flush();
                                            writer.println("$");
                                            writer.flush();
                                            //System.out.println("换人");
                                        }
                                    } else if("1".equals(s[2])) { //同意复仇
                                        JOptionPane.showMessageDialog(null, "对方接受挑战，正在开始！");
                                    } else { //不同意复仇
                                        JOptionPane.showMessageDialog(null, "对方拒绝挑战，正在重新匹配");
                                        writer.println("$");
                                        writer.flush();
                                    }
                                    board.onlineAgain = true;
                                    board.fc.solveOnline();
                                } else {
                                    int xx = Integer.valueOf(s[2]);
                                    int yy = Integer.valueOf(s[3]);
                                    board.rule.movePieceRule(board.piece[xx][yy], xx, yy);
                                    board.setPiece(xx, yy, board.strOtherColor, board.otherColor);
                                    board.step++;
                                    board.waitOther = false;
                                }
                            }
                        } catch (Exception e) {
                            continue;
                        }

                    } //end while
                } catch (Exception e1) {
                    e1.printStackTrace();
                } //end try

            } // end run
        }); // end getThread
        getThread.start();
    } // end go

    public void killed() {
        //System.out.println("killed");
        writer.println("@\n");
        writer.flush();
        try {
            socket.close();
            board.lianji = false;
            board.onlineEnd = true;
        } catch(Exception ex) {
            //System.out.println("close failed!");
        }
    }

    public static void getServer() throws IOException {
        FileInputStream fis = new FileInputStream("client.conf");
        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
        BufferedReader br = new BufferedReader(isr);

        String line = "";
        String arr[] = null;

        for (int i = 0; i < 2; ++i) {
            if ((line = br.readLine()) != null) {
                arr = line.split("=");
                if (i == 0) {
                    host = arr[1];
                } else {
                    port = Integer.parseInt(arr[1]);
                }
            } else {
                JOptionPane.showMessageDialog(null, "client.conf 打开失败", "错误信息", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

    }
}