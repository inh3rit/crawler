package com.zxsoft.crawler.s.test.socket;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import name.iaceob.kit.disgest.Disgest;
import name.iaceob.kit.id.IdKit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by iaceob on 2015/11/15.
 */
public class testSo {

    private static final Logger log = LoggerFactory.getLogger(testSo.class);
    private static Integer BUFSIZE = 32;
    private String address = "127.0.0.1";// 連線的 ip

    @Test
    public void ttd1() {
        Socket client = new Socket();
        int port = 8090;// 連線的 port
        InetSocketAddress isa = new InetSocketAddress(this.address, port);
        try {
            client.connect(isa, 10000);
            BufferedOutputStream out = new BufferedOutputStream(client
                    .getOutputStream());
            // 送出字串
            Record r = new Record();
            r.set("id", IdKit.run.generateID()).set("ecr", Disgest.encodeBase64(IdKit.run.genId()));
            out.write(JsonKit.toJson(r).getBytes());
            out.flush();
            out.close();
            out = null;
            client.close();
            client = null;

        } catch (java.io.IOException e) {
            System.out.println("Socket 連線有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }


//    @Test
//    @Deprecated
//    public void ttd2() {
//
//        String ip = "127.0.0.1";
//        Integer port = 8099;
//        JobEntity je = new JobEntity();
//
//        je.setJobId(1L).setKeyword("xxx").setUrl("http://ss.com").setType("Xtes");
//        String a = "asdf";
//
//        InputStream in;
//        OutputStream out;
//        try {
//            Socket socket = new Socket(ip, port);
//
//            in = socket.getInputStream();
//            out = socket.getOutputStream();
//
//            out.write(a.getBytes());
//            socket.shutdownOutput();
//
//
//            String res = "";
//            int recvMsgSize;
//            byte[] receiveBuf = new byte[BUFSIZE];
//            while ((recvMsgSize = in.read(receiveBuf)) != -1) {
//                res += new String(receiveBuf, 0, recvMsgSize);
//            }
//            ExchangeEntity ee = JSON.parseObject(res, ExchangeEntity.class);
//            log.debug(JsonKit.toJson(ee));
//            if (ee.getStat()<0) {
//                log.error(ee.getMsg());
//                return;
//            }
//            return;
//        } catch (UnknownHostException e) {
//            log.error(e.getMessage());
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }


//    @Test
//    public void ttd4() {
//        Boolean res = ClientModel.dao.completeRunJob("127.0.0.1", 9999, 1,
//                ClientKit.getMachineId(), ClientKit.getIp4(), 128919L);
//        log.debug("complete job : {}", res);
//    }

}
