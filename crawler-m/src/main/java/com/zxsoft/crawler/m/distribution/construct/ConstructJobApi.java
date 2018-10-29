package com.zxsoft.crawler.m.distribution.construct;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.JobType;

/**
 * Created by cox on 2016/1/9.
 */
public interface ConstructJobApi {

    /**
     *
     * 构造任务接口, 部分站点的任务因为特殊性需要单独构造
     * @param je 任务
     * @param jobType 任务类型
     * @param reptile 区域
     * @return JobEntity
     */
    JobEntity construct(JobEntity je, JobType jobType, ReptileEntity reptile);
}
