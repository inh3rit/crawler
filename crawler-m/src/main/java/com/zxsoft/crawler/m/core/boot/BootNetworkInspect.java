package com.zxsoft.crawler.m.core.boot;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.zxsoft.crawler.common.api.BootApi;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.model.PropModel;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.m.thread.search.NetworkInspectThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by cox on 2015/12/3.
 */
public class BootNetworkInspect implements BootApi {

    private static final Logger log = LoggerFactory.getLogger(BootNetworkInspect.class);
    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    private ReptileEntity reptile;
    private Prop conf = PropKit.use(Const.PROCFGFILE);

    public BootNetworkInspect(ReptileEntity reptile) {
        this.reptile = reptile;
    }

    /**
     * 定时同步网络巡检任务
     * @param reptile 区域
     * @param identify 数据标识
     * @param expire 爬虫机器心跳过期时间
     * @param interval 爬虫机器心跳时间
     */
    private void startSyncInspectJob(ReptileEntity reptile, String identify, Long expire, Long interval) {
        this.ses.scheduleAtFixedRate(new NetworkInspectThread(reptile, identify, expire, interval), 0L, 10L, TimeUnit.MINUTES);
    }


    @Override
    public Boolean start() {
        this.startSyncInspectJob(this.reptile, PropModel.dao.getStr(PropKey.DATA_IDENTIFY, this.conf.get("crawler.slave.identify")),
                PropModel.dao.getLong(PropKey.HEART_EXPIRE, this.conf.getLong("crawler.slave.heart.expire")) * 1000L,
                PropModel.dao.getLong(PropKey.INTERVAL_INSPECT, this.conf.getLong("crawler.slave.network.interval.inspect")));
        return true;
    }

    @Override
    public void startMonitor(Integer port) {

    }
}
