package com.zxsoft.crawler.s.test.socket;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import name.iaceob.kit.disgest.Disgest;
import name.iaceob.kit.id.IdKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by iaceob on 2015/11/15.
 */
public class TCPEchoClient {
    private static final int BUFSIZE = 32;

    public static void main(String[] args) throws IOException {

        String server = "127.0.0.1";       // Server name or IP address
        // Convert argument String to bytes using the default character encoding
        int servPort = 8099;
        Record r = new Record();
        r.set("id", IdKit.run.generateID()).set("ecr", Disgest.encodeBase64(IdKit.run.genId()));
        byte[] data = JsonKit.toJson(r).getBytes();

        // Create socket that is connected to server on specified port
        Socket socket = new Socket(server, servPort);
        System.out.println("Connected to server...sending echo string");

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        out.write(data);  // Send the encoded string to the server
        socket.shutdownOutput();


        String data2 = "";
        int recvMsgSize;
        byte[] receiveBuf = new byte[BUFSIZE];
        while ((recvMsgSize = in.read(receiveBuf)) != -1) {
            data2 += new String(receiveBuf, 0, recvMsgSize);
        }
        System.out.println("Received: " + data2);

        socket.close();  // Close the socket and its streams
    }
}
