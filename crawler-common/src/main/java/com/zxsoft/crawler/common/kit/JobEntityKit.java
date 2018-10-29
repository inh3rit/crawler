package com.zxsoft.crawler.common.kit;

import com.zxsoft.crawler.common.entity.redis.ClientInfoEntity;
import com.zxsoft.crawler.common.entity.redis.DetailRuleEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.type.JobType;

import java.util.Map;

/**
 * Created by cox on 2016/1/17.
 */
public class JobEntityKit {

    /**
     * 将 map 转化为 JobEntity , 这并不是一个好的做法,
     * 目前因为 json 直接反序列化为 JobEntity(Record) 若有部分字段没有 get set 方法导致无法饭序列化
     * 因此先将 json 反序列化为 Map 然后在将 Map 转化为 JobEntity
     * 更好的做法应该是优化 JobEntity(Record) 使之能正常反序列化
     * 但鉴于目前 Record 的反序列化在 fastjson 中不能很好支持, 尚为做此处理
     * TODO 反序列化优化
     * @param map JobMap
     * @return JobEntity
     */
    @Deprecated
    public static JobEntity serialize(Map map) {
        JobEntity je = new JobEntity();
        for (Object key : map.keySet()) {
            je.set(key.toString(), map.get(key));
        }
        Map lreMap = (Map) map.get("listRule");
        ListRuleEntity lre = new ListRuleEntity();
        if (lreMap != null) {
            for (Object key : lreMap.keySet()){
                lre.set(key.toString(), lreMap.get(key));
            }
            lreMap.clear();
        }
        Map dreMap = (Map) map.get("detailRules");
        DetailRuleEntity dre = new DetailRuleEntity();
        if (dreMap != null) {
            for (Object key : dreMap.keySet()){
                dre.set(key.toString(), dreMap.get(key));
            }
            dreMap.clear();
        }
        Map cieMap = (Map) map.get("client");
        ClientInfoEntity cie = new ClientInfoEntity();
        if (cieMap != null) {
            for (Object key : cieMap.keySet()) {
                cie.set(key.toString(), cieMap.get(key));
            }
            cieMap.clear();
        }

        JobType type = JobType.UNKNOWN;
        Object mjt = map.get("jobType");
        if (mjt != null) {
            if (Tool.isNumber(mjt.toString()))
                type = JobType.getIndex((Integer) map.get("jobType"));

            for (JobType jt : JobType.values()) {
                if (jt.toString().equals(mjt.toString())) {
                    type = jt;
                    break;
                }
            }
        }

        Object timestamp = map.get("timestamp");
        if (timestamp instanceof Integer)
            je.setTimestamp(Long.valueOf(String.valueOf(map.get("timestamp"))));

        je.setJobType(type);
        je.setListRule(lre).setDetailRules(dre).setClient(cie);
        map.clear();
        return je;
    }

}
