package com.zxsoft.crawler.s.thread.swap;

import com.jfinal.kit.JsonKit;
import com.zxsoft.crawler.common.thread.ApplyThreadPool;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by cox on 2015/12/22.
 */
public class SwapJobThreadTest {

    private ProxyEntity genProxy() {
        ProxyEntity pe = new ProxyEntity("192.168.1.1", 5020);
        pe.setAccount("usr").setPassword("pwd");
        return pe;
    }

    @Test
    public void testThread() throws InterruptedException {
        ThreadPoolExecutor tpe;
        tpe = ApplyThreadPool.getThreadPoolExector(20);
        while (true) {
            // tpe = ApplyThreadPool.getThreadPoolExector(20);
            ProxyEntity pe = this.genProxy();
            Integer i, c;
            i = c = java.util.concurrent.ThreadLocalRandom.current().nextInt(500);
            for (; i-->0; ) {
                tpe.execute(new TestThread(pe, c, i));
            }
            pe = null;
            // tpe = null;
            TimeUnit.SECONDS.sleep(10L);
        }
    }

}

class TestThread implements Runnable {

    private ProxyEntity pe;
    private Integer count;
    private Integer index;

    public TestThread(ProxyEntity pe, Integer count, Integer index) {
        this.pe = pe;
        this.count = count;
        this.index = index;
    }

    @Override
    public void run() {
        System.out.println("Thread Name: " + Thread.currentThread().getName() + "; Count: " + this.count + "; The index: " + this.index + "; data: " + JsonKit.toJson(this.pe));
        if (this.index!=0) return;
        System.out.println("\n");
        try {
            TimeUnit.SECONDS.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}