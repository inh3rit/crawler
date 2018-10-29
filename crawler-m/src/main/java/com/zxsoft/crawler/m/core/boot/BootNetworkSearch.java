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
import com.zxsoft.crawler.m.thread.job.networksearch.push.PushUnfinishedTaskThread;
import com.zxsoft.crawler.m.thread.job.networksearch.redis.RestoreJobThread;
import com.zxsoft.crawler.m.thread.search.NetworkSearchThread;

/**
 * Created by cox on 2015/12/3.
 */
public class BootNetworkSearch implements BootApi {

	private static final Logger log = LoggerFactory.getLogger(BootNetworkSearch.class);

	private final Prop conf = PropKit.use(Const.PROCFGFILE);
	private final ReptileEntity reptile;
	private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

	public BootNetworkSearch(ReptileEntity reptile) {
		this.reptile = reptile;
	}

	/**
	 * 初始化操作
	 * @return Boolean
	 */
	private Boolean init() {
		try {
			if (!BootModel.dao.openOracleDb(this.reptile.getUrl(), this.reptile.getUsr(), this.reptile.getPasswd(),
					this.reptile.getActive(),
					PropModel.dao.getBoolean(PropKey.DEBUG, this.conf.getBoolean("crawler.debug", false)))) {
				log.error("连接数据库 Oracle: {} 失败, 请在检查配置后重试.", this.reptile.getUrl());
				return false;
			}
			log.info("连接数据库 Oracle: {} 成功.", this.reptile.getUrl());

			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			log.info("Network search boot complete.");
		}
		return false;
	}

	/**
	 * 開啓全網搜索任務獲取以及調度線程
	 *
	 * @param interval 頻率
	 * @param reptile  區域信息
	 * @param identify 標識
	 */
	private void startThreadNetworkSearch(Long interval, Long expire, ReptileEntity reptile, String identify) {
		Thread t = new Thread(new NetworkSearchThread(interval, expire, reptile, identify), "NetworkSearchThread");
		t.start();
	}

	/**
	 * 推送未完成的任务到任务执行表
	 * 每天执行一次(因为存在将正在执行的任务也推送一次问题, 不建议时间间隔设置的太短)
	 */
	private void startThreadPushUnfinishedTask() {
		this.ses.scheduleAtFixedRate(new PushUnfinishedTaskThread(), 0L, 8L, TimeUnit.HOURS);
	}

	/**
	 * 开启任务恢复线程
	 *
	 * @param interval 頻率
	 * @param reptile  區域
	 * @param identify 標識
	 */
	private void startThreadRestoreJob(Long interval, Long expire, ReptileEntity reptile, String identify) {
		this.ses.scheduleAtFixedRate(new RestoreJobThread(interval, expire, reptile, identify), 0L, 5L, TimeUnit.HOURS);
	}

	/**
	 * 启动全网搜索搜索线程
	 * @return 启动是否成功
	 */
	@Override
	public Boolean start() {
		if (!this.init())
			return false;

		// 启动全网搜索服务
		this.startThreadNetworkSearch(
				PropModel.dao.getLong(PropKey.INTERVAL_SEARCH,
						this.conf.getLong("crawler.slave.network.interval.search")),
				PropModel.dao.getLong(PropKey.HEART_EXPIRE, this.conf.getLong("crawler.slave.heart.expire")) * 1000L,
				this.reptile, PropModel.dao.getStr(PropKey.DATA_IDENTIFY, this.conf.get("crawler.slave.identify")));

		// 开启任务恢复定时任务
		this.startThreadRestoreJob(
				PropModel.dao.getLong(PropKey.INTERVAL_SEARCH,
						this.conf.getLong("crawler.slave.network.interval.search")),
				PropModel.dao.getLong(PropKey.HEART_EXPIRE, this.conf.getLong("crawler.slave.heart.expire")) * 1000L,
				this.reptile, PropModel.dao.getStr(PropKey.DATA_IDENTIFY, this.conf.get("crawler.slave.identify")));

		// 未完成任务推送定时任务
		this.startThreadPushUnfinishedTask();
		return true;
	}

	@Override
	public void startMonitor(Integer port) {
		BootModel.dao.openMonitor(PathKit.getRootClassPath() + "/webapp", "/", port);
	}

}
