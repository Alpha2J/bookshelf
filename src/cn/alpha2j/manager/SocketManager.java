package cn.alpha2j.manager;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by cn.alpha2j on 2016/11/26.
 */
public class SocketManager {

    public static void close(Socket socket) {
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
