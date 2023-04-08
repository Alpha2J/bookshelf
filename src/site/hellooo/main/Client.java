package cn.alpha2j.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by cn.alpha2j on 2016/11/22.
 */
public class Client {

    private Socket socket;
    private PrintStream printStream;
    private BufferedReader bufferedReader;

    public void start(String ip, int port) throws IOException {
        connect(ip, port);

        new Thread(new ReaderThread(bufferedReader)).start();//该线程读取服务器传来的数据

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));//读取控制台数据

        String line;
        while ((line = consoleReader.readLine()) != null) {
            printStream.println(line);
        }

    }


    public void start() throws IOException {
        start("127.0.0.1", 23333);
    }

    //指定连接地址和端口连接, 初始化socket和 输入输出流
    private void connect(String ip, int port) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            this.socket = socket;
        } catch (IOException e) {
            e.printStackTrace();//socket 连接不成功的话是否要结束系统?
        }

        try {
            printStream = new PrintStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Client().start();
    }

}

//用来读取服务器发来的流
class ReaderThread implements Runnable {

    private BufferedReader bufferedReader;

    public ReaderThread(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

















