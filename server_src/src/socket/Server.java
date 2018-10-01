package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


// 发送消息的格式被规定为 userId:mode:[xx]

//系统id为0
//0：1：xx 系统消息
//0：2：0  系统分配黑棋    0：2：1   系统分配白棋

//用户id为大于零的整数
//userID：1：xx 聊天
//userID：2：x：y （x，y）处落子

public class Server {
	final static String charset = "GB2312";

	public static void main(String[] args) throws UnknownHostException, IOException {

		int port = 5555;
		ServerSocket server = new ServerSocket(port);

		System.out.println("server将一直等待连接的到来");

		BlockingQueue<Thread> list = new ArrayBlockingQueue<Thread>(10);
		final ArrayList<Person> persons = new ArrayList<Person>(10);
		int cnt = 0;
		while (true) {
			cnt++;
			final Socket sock = server.accept();
			final Person p = new Person(sock, cnt, null);// 创建一个玩家
			persons.add(p);

			speak(sock, cnt + "");// 把id信息返回给客户机

			speak(sock, "0:1:您正在等待对手的加入");
			Person other = findFreePerson(persons, p);// 寻找一个空闲用户

			if (other != null) {
				speak(sock, "0:1:为您找到对手，开始游戏");
				speak(sock, "0:2:0");//黑棋
				speak(other.getSocket(), "0:1:为您找到对手，开始游戏");
				speak(other.getSocket(), "0:2:1");//白棋
				p.setOtherPerson(other);
				other.setOtherPerson(p);
			}

			// 接受信息
			Thread acceptThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						while (true) {
							String info = getInfo(sock);

							String s[] = info.split(":");

							int id = Integer.valueOf(s[0]);

							Person p = findById(persons, id);

							Person other = p.getOtherPerson();

							if (other == null) {// 如果还没有对战敌人

								speak(sock, "0:你还没有匹配到对手，请耐心等待...");

							} else if (other.getSocket().isClosed()) {// 如果你的对战敌人掉了，那么

								speak(sock, "0:对手断开了连接，正在等待新的对手");

							} else {

								speak(other.getSocket(), info);
							}
							System.out.println(info);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {

						try {
							speak(p.getOtherPerson().getSocket(), "0:1:你的对手离开了游戏");
						} catch (UnsupportedEncodingException e1){
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						p.getOtherPerson().setOtherPerson(null);
						e.printStackTrace();
					}

				}
			});

			acceptThread.start();

		}

	}

	// 读取输入流
	public static String read(InputStream in, int begin, int len) throws IOException {

		byte[] b = new byte[1024];
		in.read(b, 0, len);
		return new String(b, 0, len, charset);

	}

	// 将要发送的String消息打包成bytes发送
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

		String len = Server.read(in, 0, 8);

		String info = Server.read(in, 0, Integer.valueOf(len));

		return info;
	}

	// 从队列中找到不是自己的空闲的人
	public static Person findFreePerson(ArrayList<Person> p, Person my) {
		Person pe = null;
		for (int i = 0; i < p.size(); i++) {
			if (p.get(i).getOtherPerson() == null && p.get(i) != my) {
				pe = p.get(i);
				return pe;
			}

		}

		return pe;
	}

	public static void speak(Socket sock, String content) throws UnsupportedEncodingException, IOException {

		sock.getOutputStream().write(pack(content));
	}

	public static Person findById(ArrayList<Person> p, int id) {
		for (int i = 0; i < p.size(); i++) {
			if (p.get(i).getId() == id) {
				return p.get(i);
			}

		}
		return null;

	}

}
