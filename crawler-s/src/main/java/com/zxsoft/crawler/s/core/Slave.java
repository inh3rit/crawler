package com.zxsoft.crawler.s.core;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.model.PropModel;
import com.zxsoft.crawler.common.thread.ApplyThreadPool;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.s.kit.ClientKit;
import com.zxsoft.crawler.s.model.RestoreModel;
import com.zxsoft.crawler.s.thread.heart.HeartThread;
import com.zxsoft.crawler.s.thread.swap.SwapJobThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫从机 最终任务执行
 */
public class Slave {


    private static final Logger log = LoggerFactory.getLogger(Slave.class);
    private final Prop conf = PropKit.use(Const.PROCFGFILE);
    private JobType jobType;
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    public static Integer heartParameter = 0;

    /**
     * Slave 启动前的数据库连接配置等初始化信息
     *
     * @return Boolean
     */
    private Boolean initConf() {
        try {
            String host = this.conf.get("db.redis.host");
            int port = Integer.parseInt(this.conf.get("db.redis.port"));
            String password = this.conf.get("db.redis.auth");
            RedisPlugin rp = (password == null) ?
                    new RedisPlugin(Const.CACHE_NAME, host, port)
                    : new RedisPlugin(Const.CACHE_NAME, host, port, password);
            if (rp.start()) {
                log.info("Connect redis: {} success, CacheName: {}", this.conf.get("db.redis.host"), Const.CACHE_NAME);
            } else {
                log.error("Connect Redis: {} fail, please check your config.", this.conf.get("db.redis.host"));
                return false;
            }

            // Integer jt = this.conf.getInt("crawler.job.type");
            // this.jobType = jt < 1 || jt > 4 ? JobType.UNKNOWN : JobType.getIndex(jt);
            this.jobType = JobType.getIndex(PropModel.dao.getInt(PropKey.JOB_TYPE));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 客戶機心跳
     *
     * @param cli        当前机器标识
     * @param ip         本机 ip
     * @param maxPerform 执行任务的最大数
     */
    private void startThreadHeart(Integer cli, String ip, Integer maxPerform) {
        log.info("Client heart thread start");
        this.ses.scheduleAtFixedRate(new HeartThread(cli, ip, maxPerform), 0L, 1L, TimeUnit.MINUTES);
    }


    /**
     * 开启任务交换线程, 从这里获取任务, 并且执行爬取操作
     *
     * @param cli       当前机器标识
     * @param localIp   本机 ip
     * @param threadNum 线程数量
     */
    private void startThreadSwapJob(Integer cli, String localIp, Integer threadNum) {
        try {
            ThreadPoolExecutor tpe = ApplyThreadPool.getThreadPoolExector(threadNum);
            this.ses.scheduleAtFixedRate(new SwapJobThread(cli, localIp, this.jobType, tpe), 0L, 1L, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 恢复未完成的任务
     *
     * @return Boolean
     */
    private Boolean restoreJob(Integer cli, String ip) {
        return RestoreModel.dao.submitRestoreJob(cli, ip);
    }


    /**
     * 开启 Slave
     * TODO 爬虫机器执行任务最大数量配置待实现, 目前不能很好支持
     *
     * @return Boolean
     */
    public Boolean start() {
        try {

            Integer cli = ClientKit.getMachineId();
            String ip = ClientKit.getIp4();

            if (this.jobType == JobType.NETWORK_SEARCH && !this.restoreJob(cli, ip)) {
                log.error("restore not complete job fail");
            }
            this.startThreadHeart(cli, ip, this.conf.getInt("crawler.slave.network.execute.max"));

            do {
                Thread.sleep(500);
            } while (heartParameter == 0);
            this.startThreadSwapJob(heartParameter, ip, this.conf.getInt("crawler.slave.network.execute.max"));

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static void main(String[] args) {
        Slave slave = new Slave();
        if (!slave.initConf()) {
            log.error("初始化启动配置失败, 请检查配置后重试.");
            return;
        }
        Boolean start = slave.start();
        if (!start)
            log.error("爬虫程序启动失败");
        else
            log.info("爬虫程序启动成功");
    }


}
