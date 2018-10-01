package socket;

import java.net.Socket;

public class Person {

	private int id; //系统id为0
	//private int mode; //1.聊天 2.落子

	private Socket socket;

	private Person otherPerson;

	public Person(Socket socket, int id, Person otherPerson) {
		this.socket 			= socket;
		this.id  					= id;
		this.otherPerson 	= otherPerson;
	}

	public int getId(){
		return this.id;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public void setOtherPerson(Person other) {
		this.otherPerson = other;
	}

	public Person getOtherPerson() {
		return this.otherPerson;
	}

}
