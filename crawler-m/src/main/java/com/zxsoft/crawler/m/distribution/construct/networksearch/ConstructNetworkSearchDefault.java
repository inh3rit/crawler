package com.zxsoft.crawler.m.distribution.construct.networksearch;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.m.distribution.construct.ConstructJobApi;
import com.zxsoft.crawler.m.model.MysqlModel;
import com.zxsoft.crawler.m.model.redis.InfoModel;

/**
 * 全网搜索任务默认构造器
 */
public class ConstructNetworkSearchDefault implements ConstructJobApi {

    /**
     *
     * @param je 任务
     * @param jobType 任务类型
     * @param reptile 区域
     * @return
     */
    @Override
    public JobEntity construct(JobEntity je, JobType jobType, ReptileEntity reptile) {
        // JobEntity je2 = MysqlModel.dao.getBasicInfos(je.getSectionId(), je.getJobType());
        JobEntity je2 = InfoModel.dao.getBasicInfos(je.getSectionId(), je.getJobType());

        // 如果找不到表示当前来源不存在, 加入到 tid404 表中
        if (je2 == null) {
            MysqlModel.dao.saveTid404(je.getSectionId(), reptile);
            return null;
        }

        je.setSectionId(0);
        je.merge(je2); // 合并 job
        je2.clear(); // 清空 je2

        je.setGoInto(true);
        return je;
    }
}
