package com.zxsoft.crawler.m.distribution.construct;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.JobType;

/**
 * Created by cox on 2016/1/9.
 */
public class ConstructDefault implements ConstructJobApi {

    /**
     *
     * @param je 任务
     * @param jobType 任务类型
     * @param reptile 区域
     * @return
     */
    @Override
    public JobEntity construct(JobEntity je, JobType jobType, ReptileEntity reptile) {
        return je;
    }
}
