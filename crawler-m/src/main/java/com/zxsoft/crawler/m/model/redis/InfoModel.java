package com.zxsoft.crawler.m.model.redis;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.common.type.SyncTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 缓存同步尚未完善, 同步时会出现删除 Redis 中的数据导致任务分配时找不到基础信息错误
 * 暂时不用此类, 待完善后在考虑
 */
public class InfoModel {

    private static final Logger log = LoggerFactory.getLogger(InfoModel.class);
    public static final InfoModel dao = new InfoModel(Const.CACHE_NAME);
    private static Cache redis;

    public InfoModel(String cacheName) {
        InfoModel.redis = Redis.use(cacheName);
    }

    /**
     * 获取指定规则配置, 全网搜索获取配置仅需一个即可
     *
     * @param confList 配置列表
     * @param tid      来源 id
     * @return Record
     */
    private Record getConfList(List<Record> confList, Integer tid) {
        for (Record conf : confList) {
            if (!conf.getInt("job_type").equals(JobType.NETWORK_SEARCH.getIndex())) continue;
            if (!conf.getInt("tid").equals(tid)) continue;
            return conf;
        }
        return null;
    }


    public JobEntity getBasicInfos(Integer tid) {
        return this.getBasicInfos(tid, JobType.NETWORK_SEARCH);
    }

    public JobEntity getBasicInfos(Integer tid, JobType type) {
        Record conf = this.getConfList(SyncModel.dao.getAllInfoByTable(SyncTable.V_CONF_LIST), tid);
        if (conf == null) return null;

        ListRuleEntity lre = new ListRuleEntity();
        lre.setAjax(conf.get("ajax") != null ? conf.getBoolean("ajax") : false)
                .setCategory(conf.get("category") != null ? conf.getStr("category") : null)
                .setListdom(conf.get("listdom") != null ? conf.getStr("listdom") : null)
                .setLinedom(conf.get("linedom") != null ? conf.getStr("linedom") : null)
                .setUrldom(conf.get("urldom") != null ? conf.getStr("urldom") : null)
                .setDatedom(conf.get("datedom") != null ? conf.getStr("datedom") : null)
                .setUpdatedom(conf.get("updatedom") != null ? conf.getStr("updatedom") : null)
                .setSynopsisdom(conf.get("synopsisdom") != null ? conf.getStr("synopsisdom") : null)
                .setAuthordom(conf.get("authordom") != null ? conf.getStr("authordom") : null);

        JobEntity je = new JobEntity();
        je.setJobType(type)
                .setSource_name(conf.getStr("source_name"))
                // 2016-01-03 修改, 删除重新设置来源 id, 来源 id 在全网搜索中直接从 oracle 中即查询出, 并不是此处的 tid
                // .setSource_id(conf.getInt("tid")) //
                .setSectionId(conf.getInt("section_id"))
                .setType(conf.getStr("section_type"))
                .setProvince_code(conf.get("province_id") != null ? conf.getInt("province_id") : 10000)
                .setCity_code(conf.get("city_id") != null ? conf.getInt("city_id") : 10000)
                .setLocationCode(conf.get("area_id") != null ? conf.getInt("area_id") : 10000)
                .setCountry_code(conf.getInt("region"))
                .setKeywordEncode(conf.get("keywordencode") != null ? conf.getStr("keywordencode") : "UTF-8")
                .setCookie(conf.get("cookie") != null ? conf.getStr("cookie") : null)
                .setListRule(lre);

        if (type == JobType.NETWORK_SEARCH)
            je.setUrl(conf.getStr("url"));

        return je;

//        Record website = SyncModel.dao.getWebsiteByTid(tid);
//        if (website == null) return null;
//        Record section = SyncModel.dao.getSectionBySite(website.getInt("id"));
//        if (section == null) return null;
//        Record confList = SyncModel.dao.getConfList(section.getStr("url"));
//        if (confList == null) return null;
//        ListRuleEntity lre = new ListRuleEntity();
//        lre.setAjax(confList.get("ajax") != null ? confList.getBoolean("ajax") : false)
//                .setCategory(section.get("category") != null ? section.getStr("category") : null)
//                .setListdom(confList.get("listdom") != null ? confList.getStr("listdom") : null)
//                .setLinedom(confList.get("linedom") != null ? confList.getStr("linedom") : null)
//                .setUrldom(confList.get("urldom") != null ? confList.getStr("urldom") : null)
//                .setDatedom(confList.get("datedom") != null ? confList.getStr("datedom") : null)
//                .setUpdatedom(confList.get("updatedom") != null ? confList.getStr("updatedom") : null)
//                .setSynopsisdom(confList.get("synopsisdom") != null ? confList.getStr("synopsisdom") : null)
//                .setAuthordom(confList.get("authordom") != null ? confList.getStr("authordom") : null);
//        JobEntity je = new JobEntity();
//        je.setJobType(type)
//                // .setUrl(r.getStr("url"))
//                .setSource_name(website.getStr("comment"))
//                .setSource_id(website.getInt("tid"))
//                .setSectionId(section.getInt("id"))
//                .setType(section.getStr("comment"))
//                .setProvince_code(website.get("provinceId") != null ? website.getInt("provinceId") : 10000)
//                .setCity_code(website.get("cityId") != null ? website.getInt("cityId") : 10000)
//                .setLocationCode(website.get("areaId") != null ? website.getInt("areaId") : 10000)
//                .setCountry_code(website.getInt("region"))
//                .setKeywordEncode(section.get("section") != null ? section.getStr("keywordEncode") : "UTF-8")
//                .setListRule(lre);
//        if (type == JobType.NETWORK_SEARCH) je.setUrl(section.getStr("url"));
//        return je;

    }


}
