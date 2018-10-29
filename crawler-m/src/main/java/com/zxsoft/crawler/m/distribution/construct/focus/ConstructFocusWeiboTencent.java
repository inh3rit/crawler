package com.zxsoft.crawler.m.distribution.construct.focus;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.m.distribution.construct.ConstructJobApi;
import com.zxsoft.crawler.m.model.MysqlModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 腾讯微博任务构造
 */
public class ConstructFocusWeiboTencent implements ConstructJobApi {

    private static final Logger log = LoggerFactory.getLogger(ConstructFocusWeiboTencent.class);

    /**
     *
     * @param je 任务
     * @param jobType 任务类型
     * @param reptile 区域
     * @return
     */
    @Override
    public JobEntity construct(JobEntity je, JobType jobType, ReptileEntity reptile) {
        JobEntity basicJob = MysqlModel.dao.getBasicInfos(je.getSectionId(), jobType);
        basicJob.getListRule()
                .set("replycntdom", ".replyBox .msgCnt")
                .set("replyurl", ".mask");
        return basicJob;
    }
}
