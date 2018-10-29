package com.zxsoft.crawler.s.test.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by iaceob on 2015/11/15.
 */
public class SocketServer extends java.lang.Thread {

    private boolean OutServer = false;
    private ServerSocket server;
    private final int ServerPort = 8090;// 要監控的 port

    public SocketServer() {
        try {
            server = new ServerSocket(ServerPort);

        } catch (java.io.IOException e) {
            System.out.println("Socket 啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }

    public void run() {
        Socket socket;
        java.io.BufferedInputStream in;

        System.out.println("伺服器已啟動 !");
        while (!OutServer) {
            socket = null;
            try {
                synchronized (server) {
                    socket = server.accept();
                }
                System.out.println("取得連線 : InetAddress ="
                        + socket.getInetAddress());
                // TimeOut 時間
                socket.setSoTimeout(15000);

                in = new java.io.BufferedInputStream(socket.getInputStream());
                byte[] b = new byte[1024];
                String data = "";
                int length;
                while ((length = in.read(b)) > 0)// <=0 的話就是結束了
                {
                    data += new String(b, 0, length);
                }

                System.out.println("我取得的值:" + data);
                TimeUnit.SECONDS.sleep(5L);
                in.close();
                in = null;
                socket.close();
                System.out.println("final");

            } catch (java.io.IOException e) {
                System.out.println("Socket 連線有問題 !");
                System.out.println("IOException :" + e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String args[]) {
        (new SocketServer()).start();
    }

}