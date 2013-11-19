package Transport;

/**
 * Created with IntelliJ IDEA.
 * User: shmelev
 * Date: 02.10.12
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */

import Threads.ClientThread;

import java.net.*;
import java.io.*;

public class SocketTransport {
    private ServerSocket serverSocket = null;
    private int port = 0;
    public static int threadId = 0;
    public ClientThread[] arrayClientSocket = null;

    public SocketTransport(int port) {
        this.port = port;
        this.arrayClientSocket = new ClientThread[100];
        System.out.println("SocketTransport constructed");
    }

    private ServerSocket createSocket(int port) {
        try {
            serverSocket = new ServerSocket(port);
            this.port = port;
            System.out.println("Socket created");
        } catch (BindException be) {
            serverSocket = null;
            System.out.println("This port allready use");
        } catch (IOException e) {
            serverSocket = null;
            System.out.println("Socket not created");
        }
        return this.serverSocket;
    }

    private boolean closeSocket() {
        try {
            serverSocket.close();
            System.out.println("Socket closed");
        } catch (IOException e) {
            System.out.println("Socket not closed");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void waitConnection() {

        if (createSocket(port) == null) {
            return;
        }
        System.out.println("Wait connection start");
        boolean stop_flag = false;

        while (!stop_flag) {
            try {
                Socket newSocket = serverSocket.accept();
                new ClientThread(newSocket, threadId, arrayClientSocket).start();
                threadId++;
            } catch (IOException e) {
                //this.closeSocket();
                System.out.println("Error wait connection");
                //e.printStackTrace();
            }

        }
        this.closeSocket();
        System.out.println("Wait connection cycle close");
    }

    private synchronized void shutdownServer() throws IOException {
        // обрабатываем список рабочих коннектов, закрываем каждый
        for (int i = 0; i < arrayClientSocket.length; i++) {
            if ((arrayClientSocket[i] != null)) {
                arrayClientSocket[i].getSocket().close();
            }
        }

        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
