package socket;

import java.net.Socket;

public class Person {

	private int id; // 系统id为0
	// private int mode; //1.聊天 2.落子

	private Socket socket;

	private Person otherPerson;

	public boolean start;

	public Person(Socket socket, int id, Person otherPerson, boolean start) {
		this.socket = socket;
		this.id = id;
		this.otherPerson = otherPerson;
		this.start = start;
	}

	public int getId() {
		return this.id;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public void setStart(boolean start) {
		this.start = start;
	}
	public boolean getStart() {
		return start;
	}

	public void setOtherPerson(Person other) {
		this.otherPerson = other;
	}

	public Person getOtherPerson() {
		return this.otherPerson;
	}

}
