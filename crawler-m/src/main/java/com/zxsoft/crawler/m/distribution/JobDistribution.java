/**
 *	爬虫任务分发包
 * @author wg
 */
package com.zxsoft.crawler.m.distribution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.redis.ClientInfoEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.kit.DNSCache;
import com.zxsoft.crawler.common.type.FocusStat;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.m.distribution.construct.ConstructDefault;
import com.zxsoft.crawler.m.distribution.construct.ConstructJobApi;
import com.zxsoft.crawler.m.distribution.construct.focus.ConstructFocusWeiboTencent;
import com.zxsoft.crawler.m.distribution.construct.networksearch.ConstructNetworkSearchDefault;
import com.zxsoft.crawler.m.distribution.construct.networksearch.ConstructNetworkSearchWeiboTencent;
import com.zxsoft.crawler.m.model.MysqlModel;
import com.zxsoft.crawler.m.model.job.ValidatorModel;
import com.zxsoft.crawler.m.model.oracle.FocusModel;
import com.zxsoft.crawler.m.model.oracle.OracleModel;
import com.zxsoft.crawler.m.model.redis.ClientModel;
import com.zxsoft.crawler.m.model.redis.JobModel;
import com.zxsoft.crawler.m.model.redis.SwapModel;

/**
 * 任务分发工具类, 将编辑完成的任务按类型分发给爬虫机器
 * @author wg
 */
public class JobDistribution {

	/**
	 * 日志操作对象
	 */
    private static final Logger log = LoggerFactory.getLogger(JobDistribution.class);

    /**
     * 本类实例
     */
    public static final JobDistribution instance = new JobDistribution();


    /**
     * 分发全网搜索任务到爬虫机执行
     *
     * @param je       从 Oracle 中查询出的任务
     * @param reptile  区域
     * @param identify 标识
     * @param expire   爬虫机器心跳过期时间
     * @return true false
     * @throws CrawlerException
     */
    private JobEntity constructNetworkSearch(JobEntity je, ReptileEntity reptile, String identify, Long expire) throws CrawlerException {
        je.setIdentify_md5(identify);

        /**
         * 任务接口对象
         */
        ConstructJobApi api;
        switch (je.getSource_name().trim()) {
            case "腾讯微博":
                api = new ConstructNetworkSearchWeiboTencent();
                break;
            default:
                api = new ConstructNetworkSearchDefault();
                break;
        }

        JobEntity j2 = api.construct(je, JobType.NETWORK_SEARCH, reptile);
        if (j2 == null) return null;

        je.merge(j2);
        String ip = DNSCache.getIp(je.getUrl());
        if (StrKit.notBlank(ip)) {
            je.setIp(ip);
            je.setLocation(OracleModel.dao.getLocation(ip));
            je.setLocationCode(OracleModel.dao.getLocationCode(ip));
        }
        return je;
    }


    /**
     * 分配巡检任务到执行机器
     *
     * @param je       任务
     * @param reptile  区域(在境外爬虫中因为无需做来源标记信息, 实际中这里并没有使用)
     * @param identify 数据标识
     * @param expire   爬虫机器过期时间
     * @return Boolean
     * @throws CrawlerException
     */
    public JobEntity constructNetworkInspect(JobEntity je, ReptileEntity reptile, String identify, Long expire) throws CrawlerException {
        je.setSectionId(0);
        String ip = DNSCache.getIp(je.getUrl());
        if (StrKit.notBlank()) {
            je.setIp(ip);
        }
        je.setIdentify_md5(identify);
        je.setGoInto(true);

        return je;
    }

    /**
     * 分发重点关注任务到执行机器
     * @param je       任务
     * @param reptile  区域信息
     * @param identify 数据标识
     * @param expire   爬虫机器过期时间
     * @return Boolean
     * @throws CrawlerException
     */
    public JobEntity constructNetworkFocus(JobEntity je, ReptileEntity reptile, String identify, Long expire) throws CrawlerException {
        try {


            je.setIdentify_md5(identify);

            // 获取统一的任务构造器
            ConstructJobApi api;

            switch (je.getSource_name().trim()) {
                case "腾讯微博":
                    // 验证链接是否为腾讯微博的账户链接
                    if (!je.getUrl().contains("t.qq.com")) {
                        FocusModel.dao.updateStat(je.getJobId(), FocusStat.UNUSUAL);
                        throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR, "Wrong tencent weibo account");
                    }
                    api = new ConstructFocusWeiboTencent();
                    break;
                default:
                    api = new ConstructDefault();
                    break;
            }

            je.merge(api.construct(je, JobType.NETWORK_FOCUS, reptile));


            // 获取用户的最后发文时间, 并写入到数据库中
            Long ts = ValidatorModel.dao.getLastPostTime(je.getSource_name(), je.getUrl(), je.getCookie());
            FocusModel.dao.updateLastPostTime(je.getJobId(), ts);

            // 如果此账户没有用户名, 表示为新加账户, 就需要对此账户进行验证
            if (StrKit.isBlank(je.getUsername())) {
                String userName = ValidatorModel.dao.validatorFocus(je.getSource_name(), je.getUrl(), je.getNickName(), je.getCookie());
                // 如果未成功返回用户名, 表示是非正常的账户, 修改用户状态, 并抛出异常
                if (StrKit.isBlank(userName)) {
                    FocusModel.dao.updateStat(je.getJobId(), FocusStat.UNUSUAL);
                    throw new CrawlerException(CrawlerException.ErrorCode.VALIDATOR_ERROR_FOCUS, "validator focus source [" + je.getSource_name() + "], url [" + je.getUrl() + "], account [" + je.getNickName() + "] fail.");
                }
                // 正常账户修改用户名, 修改状态成功
                FocusModel.dao.updateUserName(je.getJobId(), userName);
                FocusModel.dao.updateStat(je.getJobId(), FocusStat.NORMAL);
                je.setUsername(userName);
                // 若是第一次抓取, 不写入上次抓取的时间
                je.setTimestamp(0L);
            } else {
                // 只有是正常账户且非第一次抓取才将上次抓取的时间写入到 job 中
                je.setTimestamp(ts);
            }

            String ip = DNSCache.getIp(je.getUrl());
            if (StrKit.notBlank(ip)) {
                je.setIp(ip);
                je.setLocation(OracleModel.dao.getLocation(ip));
                je.setLocationCode(OracleModel.dao.getLocationCode(ip));
            }
            je.setIdentify_md5(identify);
            je.setGoInto(false);

            return je;
        } catch (Exception e) {
            // 进入这个 Exception 通常是原网站网路问题, 该链接非正确的链接, HttpKit 抛出异常
            // 如果出现这种情况也判定账户不可用 修改用户状态
            // TODO 因为并不一定所有的 exception 都是链接错误, 若连接超时也会如此, 暂不做处理
//            if (!FocusModel.dao.updateStat(je.getJobId(), FocusStat.UNUSUAL))
//                log.debug("Update focus unusual fail");
//            if (je.getJobId()==901L)
//                log.debug("901");
            throw e;
        }
    }

    /**
     * 分发任务
     * @param je       任务
     * @param reptile  区域
     * @param identify 数据标识
     * @param expire   爬虫机器过期时间
     * @return Boolean 分发任务是否成功
     * @throws CrawlerException 爬虫自定义异常
     */
    public Boolean emit(JobType type, JobEntity je, ReptileEntity reptile, String identify, Long expire) throws CrawlerException {

        je.setJobType(type);

        /*
        根据不同类型的任务来构造相应的任务信息
         */
        JobEntity emitJe;
        switch (je.getJobType()) {
            case NETWORK_SEARCH:
                emitJe = this.constructNetworkSearch(je, reptile, identify, expire);
                break;
            case NETWORK_INSPECT:
                emitJe = this.constructNetworkInspect(je, reptile, identify, expire);
                break;
            case NETWORK_FOCUS:
                emitJe = this.constructNetworkFocus(je, reptile, identify, expire);
                break;
            default:
                emitJe = null;
                break;
        }

        /*
        如果构造后的值是 null 表示构造失败, 则抛弃
         */
        if (emitJe == null) return false;


        ClientInfoEntity client = ClientModel.dao.getUseableClient(expire);
        emitJe.setWorkerId(client.getCli()).setClient(client);

        /*
        所有任务共用推送模块
         */
         if (!SwapModel.dao.swapJob(emitJe.getClient(), emitJe))
            throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR, "Swap job error, jobId: " + emitJe.getJobId());
        if (!JobModel.dao.createJob(emitJe.getClient(), emitJe))
            throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR, "Create job error, jobId: " + emitJe.getJobId());

        /*
        不同类型的任务 在任务分发完成后有不同的后续处理
         */
        switch (emitJe.getJobType()) {
            case NETWORK_SEARCH:
                if (!OracleModel.dao.delCrawlerJob(je.getJobId(), emitJe.getClient())) {
                    // throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR, "delete job from oracle error, jobId: " + je.getJobId());
                    // TODO 因为删除 Oracle 中任务若未控制好, 可能会造成重复删除, 或者循环任务等情况若直接 throw exception 会造成剩下的任务不能立即执行
                    log.error("delete job from oracle error, jobId: {}", je.getJobId());
                }
                break;
            case NETWORK_INSPECT:
                if (!MysqlModel.dao.updateInspectCrawlTime(reptile, je.getJobId())) {
                    log.error("Update inspect reptile[{}] and job [{}] prev_time error", reptile.getId(), je.getJobId());
                }
                break;
            case NETWORK_FOCUS:
                break;
            default:
                break;
        }

        // 清空当前任务
        je.clear();
        emitJe.clear();
        return true;
    }


}
