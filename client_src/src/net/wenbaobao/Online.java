package net.wenbaobao;

import java.awt.Color;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

public class Online {

    FiveBoard board = null;
    MakeChessManual record = null;
    Rule rule = null;
    JSplitPane split = null;

    BufferedReader reader;
    public PrintWriter writer;

    static String host = "47.95.120.196";
    static int port = 5555;

    public Online(FiveBoard board, MakeChessManual record) throws UnknownHostException, IOException {

        this.board = board;
        this.record = record;
        board.online = this;
        Client();
    }

    final static String charset = "GB2312";

    public static String id;

    public void Client() throws UnknownHostException, IOException {
        getServer();
        // 与服务端建立连接
        final Socket socket = new Socket(host, port);

        InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(socket.getOutputStream());

        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();


        // 接受消息的进程
        Thread getThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // id = getInfo(socket);
                    // System.out.println("您已经进入游戏,您的游戏id号码为" + id);

                    while (true) {
                        try {
                            String info = reader.readLine();
                            if(info == null) continue;

                            String s[] = info.split(":");

                            if ("-1".equals(s[0])) { // 来自系统
                                if ("0".equals(s[1])) { // 分配id
                                    id = s[2];
                                    board.OnlineId = id;
                                } else if ("1".equals(s[1])) { // 系统消息
                                    JOptionPane.showConfirmDialog(null, s[2]);
                                    // Object[] options = {"新游戏", "退出", "取消"};
                                    // JOptionPane.showOptionDialog(null, s[2], "提示", JOptionPane.YES_OPTION, 
                                    // OptionPane.INFORMATION_MESSAGE, null, options, options[0]);
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
                                } else { // 退出
                                    JOptionPane.showConfirmDialog(null, "对手退出游戏");
                                    board.onlineEnd = true;
                                }
                            } else { // 来自对手
                                Log("PersonID");
                                if ("1".equals(s[1])) {
                                    record.chat.append(s[2]+"\n");
                                } else {
                                    int xx = Integer.valueOf(s[2]);
                                    int yy = Integer.valueOf(s[3]);
                                    board.rule.movePieceRule(board.piece[xx][yy], xx, yy);
                                    board.setPiece(xx, yy, board.strOtherColor, board.otherColor);
                                    board.step++;
                                    board.waitOther = false;
                                    // if(board.hadWin) {
                                    // JOptionPane.showMessageDialog(null, board.strOtherColor + "胜利！");
                                    // }
                                }
                            }
                            System.out.println(info);
                        } catch (Exception e) {
                            continue;
                        }

                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
        getThread.start();
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
                System.out.println("Read client.conf failed!");
            }
        }

    }

    public static void Log(String str) {
        System.out.println(str);
    }

    public void runOver(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}