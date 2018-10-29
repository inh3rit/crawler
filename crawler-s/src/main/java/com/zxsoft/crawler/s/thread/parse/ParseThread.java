package com.zxsoft.crawler.s.thread.parse;

import com.jfinal.kit.JsonKit;
import com.zxsoft.crawler.common.entity.parse.CrawlerEntity;
import com.zxsoft.crawler.common.entity.parse.ParseEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.p.ctrl.NetworkDefaultParseCtrl;
import com.zxsoft.crawler.p.ctrl.NetworkFocusParseCtrl;
import com.zxsoft.crawler.p.ctrl.NetworkInspectParseCtrl;
import com.zxsoft.crawler.p.ctrl.NetworkSearchParseCtrl;
import com.zxsoft.crawler.p.kit.parse.ParseKit;
import com.zxsoft.crawler.s.model.ClientModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by cox on 2015/12/30.
 */
public class ParseThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ParseThread.class);

    private Map<String, Object> argMap;
    private JobEntity je;
    private Integer cli;
    private String localIp;

    public ParseThread(Map<String, Object> argMap) {
        this.argMap = argMap;
        this.je = (JobEntity) argMap.get("job");
        this.cli = (Integer) argMap.get("cli");
        this.localIp = (String) argMap.get("localIp");
    }

    /**
     * 根据任务类型获取相应的爬虫解析器
     *
     * @return ParseKit
     */
    private ParseKit getParseCtrl() {
        JobType type = (JobType) argMap.get("jobType");
        switch (type) {
            case NETWORK_SEARCH:
                return new NetworkSearchParseCtrl();
            case NETWORK_INSPECT:
                return new NetworkInspectParseCtrl();
            case NETWORK_FOCUS:
                return new NetworkFocusParseCtrl();
            default:
                return new NetworkDefaultParseCtrl();
        }
    }

    /**
     * 执行爬取, 调用爬虫解析类
     *
     * @return CrawlerEntity
     */
    private CrawlerEntity crawler() {
        ParseKit parseKit = this.getParseCtrl();
        CrawlerEntity ce = new CrawlerEntity();
        ParseEntity pe = null;
        try {
            log.debug("任务解析; JobId: " + je.getJobId() + "; URL: " + je.getUrl());
            ce.setStat(0).setMsg("准备解析");
            pe = parseKit.parse(argMap);
            ce.setStat(1).setMsg("解析完成");
        } catch (UnsupportedEncodingException e) {
            ce.setStat(-1).setMsg("解析失败").setExceptionMsg(e.getMessage());
            log.error(e.getMessage());
        } catch (CrawlerException e) {
            switch (e.code()) {
                case NETWORK_ERROR:
                    log.debug(e.getMessage());
                    break;
                case CONF_ERROR:
                    log.error(e.getMessage());
                    break;
                case CONF_ERROR_BLACKLIST:
                    log.debug(e.getMessage());
                    break;
                case SUCCESS:
                    break;
                default:
                    log.debug(e.getMessage());
                    break;
            }
            ce.setStat(-1).setMsg("解析失败").setExceptionMsg(e.getMessage());
        } catch (MalformedURLException | RuntimeException e) {
            ce.setStat(-1).setMsg("解析失败").setExceptionMsg(e.getMessage());
            log.debug(e.getMessage(), e);
        } catch (Exception e) {
            ce.setStat(-1).setMsg("解析失败").setExceptionMsg(e.getMessage());
            log.error(e.getMessage());
        } finally {
            if (pe != null)
                pe.clear();
        }
        return ce;
    }

    public void complete(String jobId, Integer stat) {
        if (!ClientModel.dao.completeRunJob(this.cli, this.localIp, jobId, stat))
            log.error("Create new delete job fail, jobId: {}", jobId);
    }

    @Override
    public void run() {
        try {
            String jobId = this.je.getJobId();

            CrawlerEntity ce = this.crawler();

            Integer stat = ce != null ? ce.getStat() : -1;
            this.complete(jobId, stat);

            log.debug("解析 JobId: {}; URL: {} 结果: {}", jobId, this.je.getUrl(), JsonKit.toJson(ce));
        } catch (Exception e) {
            log.error("任务爬取失败, Exception Message: {}", e.getMessage());
        }
    }

}
