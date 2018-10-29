package com.zxsoft.crawler.common.thread;


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/11/9.
 */
public class ApplyThreadPool {
    private static Logger logger = LoggerFactory.getLogger(ApplyThreadPool.class);

    public ApplyThreadPool() {
    }

    public static ThreadPoolExecutor getThreadPoolExector() {
        final ThreadPoolExecutor result = new ThreadPoolExecutor(64, 64, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(128), new CallerRunsPolicy());
        result.setThreadFactory(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        e.printStackTrace();
                        ApplyThreadPool.logger.error("Thread exception: " + t.getName(), e);
                        result.shutdown();
                    }
                });
                return t;
            }
        });
        return result;
    }

    public static ThreadPoolExecutor getThreadPoolExector(int threadsNum) {
        final ThreadPoolExecutor result = new ThreadPoolExecutor(threadsNum, threadsNum, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(threadsNum * 2), new CallerRunsPolicy());
        result.setThreadFactory(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        e.printStackTrace();
                        ApplyThreadPool.logger.error("Thread exception: " + t.getName(), e);
                        result.shutdown();
                    }
                });
                return t;
            }
        });
        return result;
    }

    public static void stop(int threadsNum) {
        ExecutorService exec = Executors.newFixedThreadPool(threadsNum);
        exec.shutdown();
    }

}
