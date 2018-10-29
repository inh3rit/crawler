/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.ctrl;

import com.zxsoft.crawler.common.entity.sync.UrlRuleEntity;
import com.zxsoft.crawler.p.ext.UrlRuleTextExtract;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;
import com.zxsoft.crawler.p.occupancy.JDBCUtils;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 施洋青
 * DATE： 16-9-1.
 */
public class UrlRuleTextExtractTest {

    List<UrlRuleEntity> urlRuleEntities = new ArrayList<>();
    Map<String, UrlRuleEntity> urlRuleMap = new HashMap<String, UrlRuleEntity>();
    ProxyEntity pe = null;

    String url = "http://www.jinbu6.com/topic/f08a5791ba7349818bd5871c184fc986.html";

    @Test
    public void testTextExtract() {
        Connection connection = JDBCUtils.getConn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select * from url_rule");
            UrlRuleEntity ruleEntity;
            while (rs.next()) {
                ruleEntity = new UrlRuleEntity();
                ruleEntity.setId(rs.getInt(1));
                ruleEntity.setHost(rs.getString(2));
                ruleEntity.setSiteUrl(rs.getString(3));
                ruleEntity.setContentElement(rs.getString(4));
                ruleEntity.setTimeElement(rs.getString(5));
                urlRuleEntities.add(ruleEntity);
            }

            for (UrlRuleEntity entity : urlRuleEntities) {
                urlRuleMap.put(entity.getHost(), entity);
            }

            String ruu = UrlRemake.remake(url);
            HttpEntity he = HttpKit.get(ruu, null, null, pe, null, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT, true);
            String detailPageHtml = he.getHtml();
            String content = UrlRuleTextExtract.getUrlRuleTextExtract(urlRuleMap).geText(detailPageHtml, he.getUrl());
            if (content.equals(UrlRuleTextExtract.URL_RULE_RETURN_TEXT_IS_NULL) || content.equals(UrlRuleTextExtract.URL_RULE_NOT_IN_CACHEMAP)) {
                if (content.equals(UrlRuleTextExtract.URL_RULE_RETURN_TEXT_IS_NULL)) {
                    System.out.println("解析失败,有规则无结果：\n" + detailPageHtml);
                } else {
                    System.out.println("解析失败,无规则：\n" + detailPageHtml);
                }
            } else {
                System.out.println("解析成功：\n" + content);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {

            JDBCUtils.close(connection, stmt, rs);
        }

    }
}