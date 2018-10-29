package com.zxsoft.crawler.s.test.socket;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by iaceob on 2015/11/15.
 */
public class TCPEchoServer {

    private static final int BUFSIZE = 32;   // Size of receive buffer

    public static void main(String[] args) throws IOException {

        int servPort = 8099;

        // Create a server socket to accept client connection requests
        ServerSocket servSock = new ServerSocket(servPort);

        int recvMsgSize;   // Size of received message
        byte[] receiveBuf = new byte[BUFSIZE];  // Receive buffer

        while (true) { // Run forever, accepting and servicing connections
            Socket clntSock = servSock.accept();     // Get client connection

            SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
            System.out.println("Handling client at " + clientAddress);

            InputStream in = clntSock.getInputStream();
            OutputStream out = clntSock.getOutputStream();

            // Receive until client closes connection, indicated by -1 return
            String data = "";
            while ((recvMsgSize = in.read(receiveBuf)) != -1) {
                data += new String(receiveBuf, 0, recvMsgSize);
            }
            Record r = new Record();
            r.set("id", "ssss").set("now", System.currentTimeMillis());
            out.write(JsonKit.toJson(r).getBytes());
            clntSock.shutdownOutput();

            System.out.println(data);

            clntSock.close();  // Close the socket.  We are done with this client!
        }
    /* NOT REACHED */
    }
}
