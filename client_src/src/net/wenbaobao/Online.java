package net.wenbaobao;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

    public Online(FiveBoard board, MakeChessManual record) throws UnknownHostException, IOException {

        this.board 	= board;
        this.record = record;
        Client();
    }

    final static String charset = "GB2312";

    private static String id;

    public void Client() throws UnknownHostException, IOException {

        // 要连接的服务端IP地址和端口
        String host = "47.95.120.196";
        //String  host = "127.0.0.1";
        int port = 5555;
        // 与服务端建立连接
        final Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();

        // 发送消息进程
        Thread sendThread = new Thread(new Runnable() {

            public void run() {
                //Scanner sc = new Scanner(System.in);
                while (true) {

                    if(board.lianjichat.length() > 0) {
                        try {
                            socket.getOutputStream().write(pack(id + ":1:" + board.lianjichat));
                            Log(id + ":1:" + board.lianjichat);
                            board.lianjichat = "";
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Log(board.lianjichat + "none");
                        //board.lianjichat = "";
                    }

                    if(board.lianjiX != -1 && board.lianjiY != -1) {
                        try {
                            socket.getOutputStream().write(pack(id + ":2:" + board.lianjiX + ":" + board.lianjiY));
                            Log(id + ":2:" + board.lianjiX + ":" + board.lianjiY);
                            board.lianjiX = -1;
                            board.lianjiY = -1;
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {
                        Log(board.lianjiX + "," + board.lianjiY);
                        //board.lianjiX = -1;
                        //board.lianjiY = -1;
                    }

                }

            }
        });

        // 接受消息的进程
        Thread getThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    id = getInfo(socket);
                    System.out.println("您已经进入游戏,您的游戏id号码为" + id);

                    while (true) {
                        try {
                            String info = getInfo(socket);

                            String  s[] = info.split(":");

                            if("0".equals(s[0])) {
                                if("1".equals(s[1])) {
                                    JOptionPane.showConfirmDialog(null, s[2]);
                                    //Object[] options = {"新游戏", "退出", "取消"};
                                    //JOptionPane.showOptionDialog(null, s[2], "提示", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                                } else {
                                    if("0".equals(s[2])) {
                                        board.personColor 	= Color.black;
                                        board.strPersonColor = "black";
                                        board.otherColor 	= Color.white;
                                        board.strOtherColor = "white";
                                        board.waitOther     = false;
                                    } else {
                                        board.personColor 	= Color.white;
                                        board.strPersonColor = "white";
                                        board.otherColor 	= Color.black;
                                        board.strOtherColor = "black";
                                        board.waitOther		= true;
                                    }
                                }
                            } else {
                                Log("PersonID");
                                if ("1".equals(s[1])) {
                                    record.chat.append(s[2]);
                                } else {
                                    int xx = Integer.valueOf(s[2]);
                                    int yy = Integer.valueOf(s[3]);
                                    board.rule.movePieceRule(board.piece[xx][yy], xx, yy);
                                    board.setPiece(xx, yy, board.strOtherColor, board.otherColor);
                                    board.step ++;
                                    board.waitOther = false;
                                    //if(board.hadWin) {
                                    //JOptionPane.showMessageDialog(null, board.strOtherColor + "胜利！");
                                    //}
                                }
                            }
                            System.out.println(info);
                        } catch (Exception e) {
                            continue;
                        }

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // TODO Auto-generated method stub
                catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        });
        getThread.start();
        sendThread.start();

    }

    public static void Log(String str) {
        System.out.println(str);
    }

    // 读取输入流
    public static String read(InputStream in, int begin, int len) throws IOException {

        byte[] b = new byte[1024];
        in.read(b, 0, len);
        return new String(b, 0, len, charset);

    }

    public static byte[] pack(String info) throws UnsupportedEncodingException {
        byte b[] = info.getBytes(charset);
        String len = b.length + "";
        while (len.length() < 8) {
            len = "0" + len;
        }

        String s = len + info;

        return s.getBytes(charset);

    }

    // 从socket中获取打包过来的数据，先获取长度，再获取真正的内容
    public static String getInfo(Socket socket) throws Exception {

        InputStream in = socket.getInputStream();
        byte b[] = new byte[1024];

        String len = read(in, 0, 8);

        String info = read(in, 0, Integer.valueOf(len));

        return info;
    }


    public void runOver(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}