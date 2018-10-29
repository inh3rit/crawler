package com.zxsoft.crawler.m.distribution.construct.networksearch;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.m.distribution.construct.ConstructJobApi;
import com.zxsoft.crawler.m.model.MysqlModel;
import com.zxsoft.crawler.m.model.redis.InfoModel;

/**
 * 全网搜索 腾讯微博任务构造器
 */
public class ConstructNetworkSearchWeiboTencent implements ConstructJobApi {

    /**
     *
     * @param je 任务
     * @param jobType 任务类型
     * @param reptile 区域
     * @return
     */
    @Override
    public JobEntity construct(JobEntity je, JobType jobType, ReptileEntity reptile) {
        JobEntity je2 = InfoModel.dao.getBasicInfos(je.getSectionId(), je.getJobType());

        if (je2 == null) {
            MysqlModel.dao.saveTid404(je.getSectionId(), reptile);
            return null;
        }

        je.merge(je2);
        je.setGoInto(false);
        ListRuleEntity lre = je.getListRule();
        lre.set("usernamedom", ".userName")
            .set("nicknamedom", ".userName a:first-child")
            .set("unamedom2", ".msgCnt a:first-child")
            .set("replycntdom", ".replyBox .msgCnt")
            .set("replyurl", ".mask");
        je.setListRule(lre);
        je2.clear();
        return je;
    }
}
