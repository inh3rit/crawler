/**
 * master  核心控制包
 * @author xiawenchao
 */
package com.zxsoft.crawler.m.core;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.api.BootApi;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.model.PropModel;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.common.type.SourceType;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.m.core.boot.BootNetworkFocus;
import com.zxsoft.crawler.m.core.boot.BootNetworkInspect;
import com.zxsoft.crawler.m.core.boot.BootNetworkSearch;
import com.zxsoft.crawler.m.model.BootModel;
import com.zxsoft.crawler.m.model.MysqlModel;
import com.zxsoft.crawler.m.model.redis.SyncModel;
import com.zxsoft.crawler.m.thread.info.ClientThread;
import com.zxsoft.crawler.m.thread.job.networksearch.redis.DeleteExpireJobThread;
import com.zxsoft.crawler.m.thread.job.networksearch.redis.JobCompleteThread;
import com.zxsoft.crawler.m.thread.job.networksearch.redis.RecordParseJobThread;
import com.zxsoft.crawler.m.thread.job.sync.SyncThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫主控启动类
 */
public class Master {

    private static final Logger log = LoggerFactory.getLogger(Master.class);

    /**
     * 获取配置文件信息
     */
    private final Prop conf = PropKit.use(Const.PROCFGFILE);

    /**
     * 获取数据库配置文件信息
     */
    private final Prop run = PropKit.use("run.properties");

    /**
     * 定时任务执行对象
     */
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    /**
     * 区域信息实体
     */
    private ReptileEntity reptile;

    /**
     * 需要同步的数据表
     */
    private SyncTable[] tables;

    /**
     * Master初始化
     *
     * @return 是否初始化成功： true为初始化成功，false为初始化失败
     */
    private Boolean initConf() {
        log.info("master is initing........");

        try {
            // 创建连接池
            DruidPlugin dpm = new DruidPlugin(this.run.get("db.mysql.url"), this.run.get("db.mysql.username"),
                    this.run.get("db.mysql.passwd"));
            dpm.setValidationQuery("select 1");
            dpm.setTestWhileIdle(true);

            // 在链接池中增加过滤器防止sql注入
            dpm.addFilter(new StatFilter());
            WallFilter wm = new WallFilter();

            // 设置数据库类型
            wm.setDbType("mysql");
            dpm.addFilter(wm);

            // 处理事务实例
            ActiveRecordPlugin arpm = new ActiveRecordPlugin(SourceType.MYSQL_CONF.getName(), dpm);
            arpm.setDialect(new MysqlDialect());
            arpm.setShowSql(this.conf.getBoolean("crawler.debug", false));

            log.info("Connecting database  mysql Starting .....");
            // 启动连接池和事务处理
            if (!dpm.start() || !arpm.start()) {
                log.error("connect database mysql: {} fail.", this.run.get("db.mysql.url"));
                return false;
            }
            log.info("connect mysql {} success", this.run.get("db.mysql.url"));

            // 根据配置文件中的区域别名获取区域信息
            this.reptile = MysqlModel.dao
                    .getReptileByAlias(this.run.get("crawler.reptile.alias", Const.DEFAULTALIAS).toUpperCase());
            if (this.reptile == null) {
                log.error("未找到当前区域爬虫机");
                return false;
            }

            // 获取reidis配置项
            String[] redisConf = this.reptile.getStr("redis").split(":");

            // 加载redis参数
            RedisPlugin rp = (redisConf.length == 2) ?
                    new RedisPlugin(Const.CACHE_NAME, redisConf[0], Integer.valueOf(redisConf[1]))
                    : new RedisPlugin(Const.CACHE_NAME, redisConf[0], Integer.valueOf(redisConf[1]), redisConf[2]);

            // 启动redis
            if (rp.start()) {
                log.info("连接 Redis: {} 成功, CacheName: {}", this.reptile.getStr("redis"), Const.CACHE_NAME);
            } else {
                log.error("连接 Redis: {} 失败, 请检查配置后重试.", this.reptile.getStr("redis"));
                return false;
            }

            // 获取需要同步的数据库表名
            this.tables = BootModel.dao.genSyncTables(this.reptile);

            // 如果未找到同步的表, 则表示未有同步表数据
            if (this.tables == null || this.tables.length == 0) {
                log.error("获取同步表失败");
                return true;
            }

            // 项目启动后首次同步
            SyncModel.dao.syncInfo(this.tables, this.reptile);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * mysql 站點信息同步線程
     *
     * @param tables   表名
     * @param reptile 区域信息实体
     */
    private void startThreadSync(SyncTable[] tables, ReptileEntity reptile) {
        if (log.isDebugEnabled()) {
            log.debug("Sync thread starting... and tables = {} , retile = {} .", tables.toString(), reptile.toString());
        }

        if (tables == null || tables.length == 0) {
            log.error("Tables Sync  failed......and  tables is empty!");
            return;
        }

        // 定时执行同步表线程
        this.ses.scheduleAtFixedRate(new SyncThread(tables, reptile), 1L, 2L, TimeUnit.HOURS);

        if (log.isDebugEnabled()) {
            log.debug("Sync thread started!");
        }

    }

    /**
     * 爬蟲機器实时檢測可用性
     *
     * @param expire 過期時間
     */
    private void startThreadClient(Long expire) {
        log.info("Check client thread starting");
        this.ses.scheduleAtFixedRate(new ClientThread(expire), 0L, 1L, TimeUnit.MINUTES);
        log.info("Check client thread has started");
    }

    /**
     * 开启任务完成操作线程
     *
     * @param type 任务类型
     */
    private void startThreadCompleteJob(JobType type) {
        log.info("starting CompleteJob thread");
        this.ses.scheduleAtFixedRate(new JobCompleteThread(type), 10L, 10L, TimeUnit.SECONDS);
        log.info(" CompleteJob thread has started!");
    }

    /**
     * 删除过期任务
     */
    private void startThreadDeleteExpireJob() {
        this.ses.scheduleAtFixedRate(new DeleteExpireJobThread(), 20L, 20L, TimeUnit.MINUTES);
    }


    /**
     * 爬虫正文解析记录保存线程
     */
    private void startThreadRecordParsedSaveJob() {
        this.ses.scheduleAtFixedRate(new RecordParseJobThread(), 1L, 3L, TimeUnit.MINUTES);
    }

    /**
     * 启动全网搜索
     *
     * @param reptile
     * @return 全网搜索操作对象
     */
    private BootApi startNetworkSearch(ReptileEntity reptile) {
        return new BootNetworkSearch(reptile);
    }

    /**
     * 启动重点关注
     *
     * @param reptile 区域信息实体
     * @return 重点关注操作对象
     */
    private BootApi startNetworkFocus(ReptileEntity reptile) {
        return new BootNetworkFocus(reptile);
    }

    /**
     * 启动网络巡检
     *
     * @param reptile 区域信息
     * @return 境外网络巡检操作对象
     */
    private BootApi startNetworkInspect(ReptileEntity reptile) {
        return new BootNetworkInspect(reptile);
    }

    /**
     * Master 启动
     *
     * @return master是否启动成功
     */
    public Boolean start() {
        log.info("master is starting....");
        // 开启同步表线程
        this.startThreadSync(this.tables, this.reptile);

        // 爬虫机器可用性检查
        this.startThreadClient(
                PropModel.dao.getLong(PropKey.HEART_EXPIRE, this.conf.getLong("crawler.slave.heart.expire")) * 10000);

        // 完成任务删除线程
        this.startThreadCompleteJob(this.reptile.getType());

        // 过期任务完成线程
        this.startThreadDeleteExpireJob();

        BootApi boot;
        switch (this.reptile.getType()) {
            case NETWORK_SEARCH:
                // 开启全网搜索线程
                boot = this.startNetworkSearch(this.reptile);
                break;
            case NETWORK_FOCUS:
                // 开启重点关注线程
                boot = this.startNetworkFocus(this.reptile);
                break;
            case NETWORK_INSPECT:
                // 开启网络巡检线程(境外巡检)
                boot = this.startNetworkInspect(this.reptile);
                break;
            default:
                return false;
        }
        // 启动线程
        if (!boot.start()) {
            return false;
        }

        // 爬虫是否需要监控
        Boolean monitorOpen = PropModel.dao.getBoolean(PropKey.MONITOR_OPEN,
                this.conf.getBoolean("crawler.monitor.open"));
        log.debug("monitor open: {}", monitorOpen);
        if (!monitorOpen) {
            log.info("master have started!");
            return true;
        }

        // 开启数据库监控
        boot.startMonitor(PropModel.dao.getInt(PropKey.MONITOR_PORT, this.conf.getInt("crawler.monitor.port")));
        log.info("master has started!");
        return true;
    }

    /**
     * master 运行主线程
     *
     * @param args
     */
    public static void main(String[] args) {
        Master master = new Master();
        if (!master.initConf()) {
            log.error("boot error, init config fail.");
            return;
        }
        if (!master.start()) {
            log.error("Crawler master server start fail.");
        }
    }

}
