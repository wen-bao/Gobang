package online;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Log {

    public void tolog(String str) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(date);

        File file = new File("server.log");

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
}