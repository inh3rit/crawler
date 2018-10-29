package com.zxsoft.crawler.s.test;

import com.alibaba.fastjson.JSON;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import org.junit.Test;

/**
 * Created by cox on 2015/9/6.
 */
public class TestJson {


    @Test
    public void test() {
        String json = "{\"city_code\":100000,\"sectionId\":1137,\"province_code\":100000,\"type\":\"搜报网\",\"keywordEncode\":\"GBK\",\"platform\":6,\"listRule\":{\"urldom\":\"li h2 a\",\"datedom\":\"p.newsInfo em.postDate\",\"listdom\":\"div#srh_main\",\"authordom\":\"\",\"updatedom\":\"\",\"category\":\"search\",\"ajax\":false,\"synopsisdom\":\"p.newCon span\"},\"url\":\"http:\\/\\/www.soubao.net\\/search\\/searchList.aspx?keyword=%s&startdate=1900-01-01&enddate=2200-12-31&timesel=custom\",\"jobId\":502302,\"country_code\":1,\"source_id\":1642,\"keyword\":\"合肥 暴雨\",\"locationCode\":100000,\"jobType\":\"NETWORK_SEARCH\",\"source_name\":\"搜报网\"}";
        JobEntity je = JSON.parseObject(json, JobEntity.class);
        ListRuleEntity lre = je.getListRule();
        System.out.println(com.jfinal.kit.JsonKit.toJson(lre));
        System.out.println(com.jfinal.kit.JsonKit.toJson(je));
        // JobConf jc = new Gson().fromJson(com.jfinal.kit.JsonKit.toJson(je), JobConf.class);
        // System.out.println(jc);
    }

    @Test
    public void testGson() {
        String json = "{\"slave\": 4156,\"identify_md5\":\"iaceob\",\"ip\":\"115.239.210.52\",\"city_code\":100000,\"goInto\":true,\"sectionId\":757,\"province_code\":100000,\"type\":\"百度新闻搜索\",\"keywordEncode\":null,\"platform\":6,\"listRule\":{\"urldom\":\"h3 a\",\"datedom\":\"div.c-summary p\",\"listdom\":\"div#content_left\",\"authordom\":\"\",\"updatedom\":\"\",\"category\":\"search\",\"ajax\":false,\"synopsisdom\":\"div.c-summary\"},\"url\":\"http:\\/\\/news.baidu.com\\/ns?word=%s&tn=news&from=news&cl=2&rn=20&ct=1\",\"recurrence\":false,\"jobId\":502596,\"country_code\":1,\"source_id\":927,\"keyword\":\"合肥 便衣\",\"locationCode\":100000,\"jobType\":\"NETWORK_SEARCH\",\"source_name\":\"百度新闻搜索\"}";
        JobEntity je = JSON.parseObject(json, JobEntity.class);
        // Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        // ListRuleEntity lre = je.getListRule();
        // ListRule lr = gson.fromJson(com.jfinal.kit.JsonKit.toJson(lre), ListRule.class);
        // JobConf jc = gson.fromJson(com.jfinal.kit.JsonKit.toJson(je), JobConf.class);
        // System.out.println(com.jfinal.kit.JsonKit.toJson(jc.getListRule()));

    }

    @Test
    public void testEq() {
        Integer a = 2;
        Integer b = 2;
        System.out.println(a.equals(b));
    }

}
