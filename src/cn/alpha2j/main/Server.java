package cn.alpha2j.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by cn.alpha2j on 2016/11/22.
 */
public class Server {

    public static void main(String[] args) {
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(23333);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new MyTask(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class MyTask implements Runnable {

    private Socket socket;

    public MyTask() {
        super();
    }

    public MyTask(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public void run() {
        LibrarySystem librarySystem = new LibrarySystem(socket);
        librarySystem.librarySystemIndex();
    }
}





