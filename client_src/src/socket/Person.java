package socket;

import java.awt.Point;
import java.net.Socket;

public class Person {

    private int id;
    private int mode; //1.请求 2.落子

    private Socket socket;

    private Point point;

    private Person otherPerson;

    public Person(int id, int mode, Socket socket, Point point, Person otherPerson) {
        this.id 			= id;
        this.mode 			= mode;
        this.socket 		= socket;
        this.point 			= point;
        this.otherPerson 	= otherPerson;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setOtherPerson(Person otherPerson) {
        this.otherPerson = otherPerson;
    }

    public int getMode() {
        return this.mode;
    }

    public Point getPoint() {
        return this.point;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public Person getOtherPerson() {
        return this.otherPerson;
    }

}