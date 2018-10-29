package com.zxsoft.crawler.m.model.redis;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.kit.JobEntityKit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cox on 2016/1/17.
 */
public class SwapModelTest {

    private static final Logger log = LoggerFactory.getLogger(SwapModelTest.class);

    @Test
    public void serializeJson() {
        String jsonStr = "{\"city_code\":100000,\"client\":{\"cli\":6165,\"total\":0,\"ip\":\"192.168.6.165\",\"ts\":1453012293518},\"cookie\":\"ptui_loginuin=3115565062; pt2gguin=o3115565062; RK=hTvasQRD2I; luin=o3115565062; lskey=00010000130f531b53551b8e80f5e236c6bc953d28a9b0726736ad36a7d51cc4d6fa0cd94ba4c7755a56cf00; p_luin=o3115565062; p_lskey=00040000dcc560e32081ea2f1100a305ce19688f2555a9c739f2e5e20f6a227cf32b2ae1ed92f2b3251baf5b; pgv_pvid=57775016; o_cookie=3115565062; ts_uid=9583793619; pgv_pvi=5328606912; dm_login_weixin_rem=; ts_refer=p.t.qq.com\\/longweibo\\/index.php; wbilang_3115565062=zh_CN; wbilang_10000=zh_CN; qm_authimgs_id=1; qm_verifyimagesession=h0141f314723e865da4289dfa2a91ef6b6601bf7677b534fe87fabae449428f73d2fd0b7373556aa4ea; pgv_si=s9991151616; qm_sid=808e12153bbcf31721d4dde04216585a,cCYtkCY75y_I.; qm_username=1775137127; qm_sk=1775137127&SE3T6fN-; qm_ssum=1775137127&60c79be9bb6a5d9f26d519a81471539f; mb_reg_from=300; pgv_info=ssid=s8343113920; g_ut=1; pt_clientip=4cd20abf81e258ab; pt_serverip=131b0a93196f4917\",\"country_code\":1,\"cyclic\":false,\"goInto\":false,\"identify_md5\":\"iaceob\",\"ip\":\"101.227.143.32\",\"jobId\":131182,\"jobType\":\"NETWORK_SEARCH\",\"keyword\":\"贴吧 买卖\",\"keywordEncode\":\"UTF-8\",\"listRule\":{\"usernamedom\":\".userName\",\"replyurl\":\".mask\",\"authordom\":null,\"linedom\":\".msgBox\",\"replycntdom\":\".replyBox .msgCnt\",\"updatedom\":null,\"ajax\":false,\"nicknamedom\":\".userName a:first-child\",\"datedom\":\"a.time\",\"urldom\":\"a.time\",\"unamedom2\":\".msgCnt a:first-child\",\"listdom\":\"#talkList\",\"category\":\"search\",\"synopsisdom\":\".msgCnt\"},\"location\":\"上海市 电信\",\"locationCode\":310000,\"platform\":3,\"province_code\":100000,\"sectionId\":1378,\"source_id\":1713,\"source_name\":\"腾讯微博\",\"type\":\"腾讯微博_全网搜索\",\"url\":\"http:\\/\\/search.t.qq.com\\/index.php?k=%s\",\"workerId\":6165}";
        Map jobMap = JSON.parseObject(jsonStr, HashMap.class);
        System.out.println(jobMap);
        jobMap.get("lre");
        System.out.println();

        JobEntity jobEntity = JSON.parseObject(jsonStr, JobEntity.class);
        System.out.println(jobEntity);

        log.debug(jobEntity.getCookie());
        log.debug(jobEntity.getNickName());

        ListRuleEntity lre = jobEntity.getListRule();
        System.out.println(lre);
        log.debug(lre.getListdom());
        log.debug(lre.getStr("usernamedom"));

        TestEntity te = JSON.parseObject(jsonStr, TestEntity.class);
        System.out.println(te.toString());
        System.out.println(te);

    }


    @Test
    public void serializeJsonMap() {
        String jsonStr = "{\"city_code\":100000,\"client\":{\"cli\":6165,\"total\":0,\"ip\":\"192.168.6.165\",\"ts\":1453012293518},\"cookie\":\"ptui_loginuin=3115565062; pt2gguin=o3115565062; RK=hTvasQRD2I; luin=o3115565062; lskey=00010000130f531b53551b8e80f5e236c6bc953d28a9b0726736ad36a7d51cc4d6fa0cd94ba4c7755a56cf00; p_luin=o3115565062; p_lskey=00040000dcc560e32081ea2f1100a305ce19688f2555a9c739f2e5e20f6a227cf32b2ae1ed92f2b3251baf5b; pgv_pvid=57775016; o_cookie=3115565062; ts_uid=9583793619; pgv_pvi=5328606912; dm_login_weixin_rem=; ts_refer=p.t.qq.com\\/longweibo\\/index.php; wbilang_3115565062=zh_CN; wbilang_10000=zh_CN; qm_authimgs_id=1; qm_verifyimagesession=h0141f314723e865da4289dfa2a91ef6b6601bf7677b534fe87fabae449428f73d2fd0b7373556aa4ea; pgv_si=s9991151616; qm_sid=808e12153bbcf31721d4dde04216585a,cCYtkCY75y_I.; qm_username=1775137127; qm_sk=1775137127&SE3T6fN-; qm_ssum=1775137127&60c79be9bb6a5d9f26d519a81471539f; mb_reg_from=300; pgv_info=ssid=s8343113920; g_ut=1; pt_clientip=4cd20abf81e258ab; pt_serverip=131b0a93196f4917\",\"country_code\":1,\"cyclic\":false,\"goInto\":false,\"identify_md5\":\"iaceob\",\"ip\":\"101.227.143.32\",\"jobId\":131182,\"jobType\":\"NETWORK_SEARCH\",\"keyword\":\"贴吧 买卖\",\"keywordEncode\":\"UTF-8\",\"listRule\":{\"usernamedom\":\".userName\",\"replyurl\":\".mask\",\"authordom\":null,\"linedom\":\".msgBox\",\"replycntdom\":\".replyBox .msgCnt\",\"updatedom\":null,\"ajax\":false,\"nicknamedom\":\".userName a:first-child\",\"datedom\":\"a.time\",\"urldom\":\"a.time\",\"unamedom2\":\".msgCnt a:first-child\",\"listdom\":\"#talkList\",\"category\":\"search\",\"synopsisdom\":\".msgCnt\"},\"location\":\"上海市 电信\",\"locationCode\":310000,\"platform\":3,\"province_code\":100000,\"sectionId\":1378,\"source_id\":1713,\"source_name\":\"腾讯微博\",\"type\":\"腾讯微博_全网搜索\",\"url\":\"http:\\/\\/search.t.qq.com\\/index.php?k=%s\",\"workerId\":6165}";
        Map jobMap = JSON.parseObject(jsonStr, HashMap.class);
        System.out.println(jobMap);

        JobEntity je = JobEntityKit.serialize(jobMap);
        System.out.println(je);

        ListRuleEntity lre = je.getListRule();
        System.out.println(lre);

    }

}