package com.zxsoft.crawler.s.thread.swap;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.sync.*;
import com.zxsoft.crawler.common.model.PropModel;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.s.model.*;
import com.zxsoft.crawler.s.thread.parse.ParseThread;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by cox on 2015/11/24.
 */
public class SwapJobThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SwapJobThread.class);

    private Integer cli;
    private ThreadPoolExecutor tpe;
    private String localIp;
    private JobType type;
    private static List<TimeRegexEntity> tre;
    private static BlacklistEntity[] bes;
    private static BlanklistEntity[] blankes;
    private static UrlRuleEntity[] ures;

    public SwapJobThread(Integer cli, String localIp, JobType type, ThreadPoolExecutor tpe) {
        this.cli = cli;
        this.tpe = tpe;
        this.localIp = localIp;
        this.type = type;
    }

    // 加载资源数据，设置成静态变量
    static {
        tre = TimeRegexModel.dao.getAllTimeRegex();
        bes = BlacklistModel.dao.getAllBlacklist();
        blankes = BlanklistModel.dao.getAllBlanklist();
        ures = UrlRuleModel.dao.getAllUrlRule();
    }

    /**
     * 从配置中获取代理服务器信息
     *
     * @return ProxyEntity
     */
    private ProxyEntity genProxyEntity() {
        ProxyEntity pe = null;
        if (StrKit.notBlank(PropModel.dao.getStr(PropKey.PROXY_HOST))
                && StrKit.notBlank(PropModel.dao.getStr(PropKey.PROXY_PORT))) {
            pe = new ProxyEntity(PropModel.dao.getStr(PropKey.PROXY_HOST), PropModel.dao.getInt(PropKey.PROXY_PORT));
            if (StrKit.notBlank(PropModel.dao.getStr(PropKey.PROXY_USR))) {
                pe.setAccount(PropModel.dao.getStr(PropKey.PROXY_USR))
                        .setPassword(PropModel.dao.getStr(PropKey.PROXY_PASSWD));
            }
        }
        return pe;
    }

    @Override
    public void run() {
        try {
            // this.syncInfo();
            ProxyEntity pe = this.genProxyEntity();
            String[] address = PropModel.dao.get(PropKey.SOLR_ADDRESS);

            List<JobEntity> jes = SwapModel.dao.getExecJob(this.cli);
            if (jes.isEmpty()) {
                log.info("No task,heart:{}", this.cli);
                return;
            }

            Map<String, Object> baseArgMap = new HashMap<String, Object>();
            baseArgMap.put("address", address);
            baseArgMap.put("localIp", localIp);
            baseArgMap.put("cli", cli);
            baseArgMap.put("proxy", pe);
            baseArgMap.put("blacks", bes);
            baseArgMap.put("timeRule", tre);
            baseArgMap.put("jobType", type);
            baseArgMap.put("blanks", blankes);
            baseArgMap.put("urlRules", ures);

            for (JobEntity je : jes) {
                Map<String, Object> argMap = new HashMap<>(baseArgMap);
                argMap.put("job", je);
                this.tpe.execute(new ParseThread(argMap));
                log.info("取得任務: {}", je.getJobId());
            }

            // 内存考虑, 若频繁的获取任务会创建很多 ProxyEntity 若 JVM 不及时释放可能会增加内存压力
            pe = null;
            address = null;

            log.info("Swap job finished");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
