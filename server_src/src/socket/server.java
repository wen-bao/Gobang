package socket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

// 发送消息的格式被规定为 userId:mode:[xx]

//系统id为-1
//-1: 0: xx 分配id
//-1：1：xx 系统消息
//-1：2：0  系统分配黑棋    -1：2：1   系统分配白棋
//-1:-1    退出

//用户id为大于零的整数
//userID：1：xx 聊天
//userID：2：x：y （x，y）处落子

public class server {
    public static void main(String[] args) throws IOException {
        new server().go();
    }

    final static String charset = "GB2312";
    final int PERSONNUM = 100;
    Person[] persons;

    public void go() throws IOException {
        persons = new Person[PERSONNUM];
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket server = new ServerSocket(getPort());

        while (true) {

            int num = 0, count = 0;
            while (num < PERSONNUM) {
                if (persons[num] != null) {
                    Log("用户" + num + "在线");
                    count++;
                }
                num++;
            }
            Log("在线人数：" + count);

            final Socket client = server.accept();

            int cnt = addClient(client);
            if (cnt != -1) {
                final Person player = new Person(client, cnt, null, false);
                persons[cnt] = player;
                task newtask = new task(player);
                executor.execute(newtask);
            }
        }

    }

    int addClient(Socket client) {
        int cnt = 0;
        try {
            while (persons[cnt] != null) {
                cnt++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("over flow");
            return -1;
        }
        return cnt;
    }

    int getPort() throws IOException {
        FileInputStream fis = new FileInputStream("server.conf");
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        String arrs[] = null;
        int port = 0;

        if ((line = br.readLine()) != null) {
            arrs = line.split("=");
            port = Integer.parseInt(arrs[1]);
            // System.out.println(arrs[0] + " " + arrs[1]);
        } else {
            System.out.println("Read server.conf failed!");
        }

        br.close();
        isr.close();
        fis.close();
        return port;
    }

    // 从队列中找到不是自己的空闲的人
    public Person findFreePerson(Person my) {
        Person pe = null;
        for (int i = 0; i < PERSONNUM; i++) {
            if (persons[i] != null && persons[i].getOtherPerson() == null && i != my.getId()) {
                pe = persons[i];
                return pe;
            }
        }
        return pe;
    }

    public synchronized boolean check(Person my) {
        return !my.getStart();
    }

    public void Log(String str) {
        System.out.println(str);
    }

    class task implements Runnable {

        private Person player;

        private BufferedReader reader;

        public task(Person player) throws IOException {
            this.player = player;
            InputStreamReader isReader = new InputStreamReader(player.getSocket().getInputStream(), charset);
            reader = new BufferedReader(isReader);
        }

        public void run() {
            try {
                speak(player.getSocket(), "-1:0:" + player.getId()); // 把id信息返回给客户机
                speak(player.getSocket(), "-1:1:您正在等待对手的加入");
                while (true) {

                    if (player.getSocket().isClosed()) {
                        persons[player.getId()] = null;
                        break;
                    }

                    if (check(player)) {
                        Person other = findFreePerson(player);// 寻找一个空闲用户

                        if (other != null) {
                            speak(player.getSocket(), "-1:1:为您找到对手，开始游戏");
                            speak(player.getSocket(), "-1:2:0");// 黑棋
                            speak(other.getSocket(), "-1:1:为您找到对手，开始游戏");
                            speak(other.getSocket(), "-1:2:1");// 白棋
                            player.setStart(true);
                            other.setStart(true);
                            player.setOtherPerson(other);
                            other.setOtherPerson(player);
                        }
                    } else {
                        String info = reader.readLine();
                        if (info == null)
                            continue;
                        Person other = player.getOtherPerson();

                        if (other.getSocket().isClosed()) {// 如果你的对战敌人掉了，那么
                            speak(player.getSocket(), "-1:-1");
                            persons[other.getId()] = null;
                            persons[player.getId()] = null;
                            break;
                        } else {
                            speak(other.getSocket(), info);
                        }
                        // System.out.println(info);
                    }
                }

            } catch (Exception e) {
                try {
                    // speak(player.getSocket(), "-1:-1");
                    persons[player.getId()] = null;
                    if (player.getOtherPerson() != null) {
                        speak(player.getOtherPerson().getSocket(), "-1:-1");
                        persons[player.getOtherPerson().getId()] = null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }

        }

        public void speak(Socket sock, String content) throws UnsupportedEncodingException, IOException {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), charset));
            writer.println(content);
            writer.flush();
        }

    }

}