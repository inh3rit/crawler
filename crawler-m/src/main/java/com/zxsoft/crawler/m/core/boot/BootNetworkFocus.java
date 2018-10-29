/**
 * 爬虫master核心启动类
 * @author wg
 */
package com.zxsoft.crawler.m.core.boot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.zxsoft.crawler.common.api.BootApi;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.model.PropModel;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.m.model.BootModel;
import com.zxsoft.crawler.m.thread.search.NetworkFocusThread;

/**
 * 重点关注启动类
 * @author wg
 */
public class BootNetworkFocus implements BootApi {

	/**
	 * 日志操作对象
	 */
    private static final Logger log = LoggerFactory.getLogger(BootNetworkFocus.class);

    /**
     * 爬虫配置文件map对象
     */
    private final Prop conf = PropKit.use(Const.PROCFGFILE);

    /**
     * 区域实体对象
     */
    private final ReptileEntity reptile;

    /**
     * 定时任务执行对象
     */
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    /**
     * 带参构造方法
     * @param reptile
     */
    public BootNetworkFocus(ReptileEntity reptile) {
        this.reptile = reptile;
    }

    /**
     * 重点关注初始化
     * @return 初始化是否成功
     */
    private Boolean init() {
        try {
            if (!BootModel.dao.openOracleDb(this.reptile.getUrl(), this.reptile.getUsr(),
                    this.reptile.getPasswd(), this.reptile.getActive(),
                    PropModel.dao.getBoolean(PropKey.DEBUG, this.conf.getBoolean("crawler.debug", false)))) {
                log.error("连接数据库 Oracle: {} 失败, 请在检查配置后重试.", this.reptile.getUrl());
                return false;
            }
            log.info("连接数据库 Oracle: {} 成功.", this.reptile.getUrl());

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            log.info("Network focus boot complete");
        }
        return false;
    }


    /**
     * 重点关注定时任务
     * @param interval 定时时间
     * @param expire 延时时间
     * @param identify 数据标识
     * @param reptile 区域信息
     */
    private void startThreadNetworkFocus(Long interval, Long expire, String identify, ReptileEntity reptile) {
        this.ses.scheduleAtFixedRate(new NetworkFocusThread(identify, expire, interval, reptile), 0L, 30L, TimeUnit.MINUTES);
    }


    /**
     * 重点关注启动
     * @return 启动是否成功
     */
    @Override
    public Boolean start() {
        if (!this.init()) {
            log.error("Start network focus error");
            return false;
        }
        this.startThreadNetworkFocus(60L, PropModel.dao.getLong(PropKey.HEART_EXPIRE, this.conf.getLong("crawler.slave.heart.expire")) * 1000L,
                PropModel.dao.getStr(PropKey.DATA_IDENTIFY, this.conf.get("crawler.slave.identify")),
                this.reptile);
        return true;
    }

    @Override
    public void startMonitor(Integer port) {
        BootModel.dao.openMonitor(PathKit.getRootClassPath() + "/webapp", "/", port);
    }
}
