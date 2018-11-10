package socket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.text.SimpleDateFormat;

// 发送消息的格式被规定为 userId:mode:[xx]

//系统id为-1
//-1: 0: xx 分配id
//-1：1：xx 系统消息
//-1：2：0  系统分配黑棋    -1：2：1   系统分配白棋
//-1:-1    退出

//用户id为大于等于零的整数
//userID：1：xx 聊天
//userID：2：x：y （x，y）处落子
//userID: 0:0 复仇
//userID: 0:1 同意复仇
//userID: 0:2 不同意复仇

//特殊格式 =_= 换人
//== 系统同意换人
//@  请求关机

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
            final Socket client = server.accept();

            int cnt = addClient(client);
            if (cnt != -1) {
                log("get a client: " + cnt);
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
                log("client close failed!");
            }
            log("persons over flow!");
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

    public void log(String str) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(date);

        File file = new File("data/server.log");

        Writer out = null;

        try {
            out = new FileWriter(file, true);
            out.write(time + ":" + str + "\n");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void speak(Socket sock, String content) throws UnsupportedEncodingException, IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), charset));
        writer.println(content);
        writer.flush();
    }

    // 从队列中找到不是自己的空闲的人
    public synchronized void findFreePerson(Person my) throws UnsupportedEncodingException, IOException{
        if(my.getStart()) return;
        for (int i = 0; i < PERSONNUM; i++) {
            Person other = persons[i];
            if (other != null && other.getOtherPerson() == null && i != my.getId()) {
                speak(other.getSocket(), "-1:1:为您找到对手，开始游戏");
                speak(other.getSocket(), "-1:2:1");// 白棋
                speak(my.getSocket(), "-1:1:为您找到对手，开始游戏");
                speak(my.getSocket(), "-1:2:0");// 黑棋
                my.setStart(true);
                other.setStart(true);
                my.setOtherPerson(other);
                other.setOtherPerson(my);
                return;
            }
        }
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
                        log("client: " + player.getId() + " leave!");
                        persons[player.getId()] = null;
                        break;
                    }

                    if (!player.start) {
                        findFreePerson(player);
                    } else {
                        Person other = player.getOtherPerson();
                        if (other.getSocket().isClosed()) {// 如果你的对战敌人掉了，那么
                            log("client: " + player.getId() + " leave!");
                            speak(player.getSocket(), "-1:-1");
                            persons[other.getId()] = null;
                            player.setOtherPerson(null);
                            player.setStart(false);
                        } else {
                            String info = reader.readLine();
                            if (info != null) {
                                if("=_=".equals(info)) {
                                    //System.out.println("changed!");
                                    speak(player.getSocket(), "-1:==");
                                    speak(other.getSocket(), "-1:=_=");
                                    other.setOtherPerson(null);
                                    other.setStart(false);
                                    player.setOtherPerson(null);
                                    player.setStart(false);
                                } else if("@".equals(info)){
                                    log("client: " + player.getId() + " leave!");
                                    persons[player.getId()] = null;
									speak(other.getSocket(), "-1:-1");
                                    break;
                                } else {
                                    speak(other.getSocket(), info);
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                try {
                    log("client: " + player.getId() + " leave!");
                    // speak(player.getSocket(), "-1:-1");
                    persons[player.getId()] = null;
                    if (player.getOtherPerson() != null) {
                        speak(player.getOtherPerson().getSocket(), "-1:-1");
                        persons[player.getOtherPerson().getId()] = null;
                    }
                } catch (Exception ex) {
                    persons[player.getOtherPerson().getId()] = null;
                }
                //e.printStackTrace();
            }

        }

    }

}
