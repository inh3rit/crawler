/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import com.zxsoft.crawler.p.cleaning.util.CosineSimilarAlgorithm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 * <p/> 目录页（列表页）评分
 */
public class CatalogPageScoreFilter extends FilterChainImpl {
    private double threshold = 0.0;

    public CatalogPageScoreFilter() {
        threshold = 0.7;
        addFilter(this);
    }

    public CatalogPageScoreFilter(double threshold) {
        this.threshold = threshold;
        addFilter(this);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public double doFilter(final Map<String, Object> objectMap) {
        String url = String.valueOf(objectMap.get(Filter.URL));
        String html = String.valueOf(objectMap.get(Filter.HTML));
        Document doc = Jsoup.parse(html);
        Elements urls = doc.getElementsByTag("a"); // 提取文本中所有的url
        String title = doc.title();// 原网页标题
        String text = doc.text(); // 原网页文本
        //int count = count(url, "/");  // 原网页url中出现/的次数

        // 判断原网页所有的url相互之间的相似度，例如url_a相似的有(url_b,url_c,url_d,url_e)，统计括号内的url数量,然后比较较多的url与总量的占比
        double source = getSource(urls);

        // 判断是否有分页，如果有增加评分
       /* boolean have_next_page = CollectionKit.isEmpty(doc.select("a:matchesOwn(下一页|下页|下一页>|Next|next)"));
        if (have_next_page)
            source += 0.1;*/

        // 判断网页标题或者有相似的文本是否存在html中其他位置，有则降低评分
        boolean is_contains = text.contains(title);
        if (is_contains)
            source -= 0.1;

        // 判断原网页中是否有文章的作者属性，例如（编辑：xxxxx|作者：xxxx|责编：xxxx|责任编辑：xxxxx）,有则降低评分 正则 ：(编辑|作者|责编|责任编辑)(:|：)
        Pattern p = Pattern.compile("(编辑|作者|责编|责任编辑|发布者|文章来源)(:|：)");
        Matcher m = p.matcher(text);
        if (m.find())
            source -= 0.2;

        return source < 0.0 ? 0.0 : source;
    }


    /**
     * 1、分析网页中有效的url与其他url之间的相似度
     * 2、提相似度较高的url
     * 3、将提取后的url进一步筛选，留下与之相似度大于等于10
     *
     * @param urls
     * @return
     */
    public static double getSource(Elements urls) {
        Map<String, Double> sourceMap = new HashMap<>();
        String str1, str2;
        double source;
        for (Element a : urls) {
            for (Element a2 : urls) {
                source = 0.0;
                str1 = a.attr("href").replace("http://", "").replace("https://", "");
                str2 = a2.attr("href").replace("http://", "").replace("https://", "");

                if (!str1.trim().equals(str2.trim()) && str1 != null && str1.length() > 0 && str2 != null && str2.length() > 0)
                    source = CosineSimilarAlgorithm.getSimilarity(str1, str2);
                // 保留相似度评分大于等于0.96
                if (source >= 0.96 && source <= 1.0) {
                    sourceMap.put(str1, sourceMap.get(str1) == null ? 1 : sourceMap.get(str1) + 1);
                }
            }
        }
        // 计算相似度数量大于等于10的占比
        double count = 0.0;
        for (Map.Entry<String, Double> entry : sourceMap.entrySet()) {
            if (entry.getValue() >= 10)
                count++;
        }

        //return count / sourceMap.size();

        if (sourceMap.size() <= 20) {
            return (count / sourceMap.size()) - 0.1;
        } else {
            return count / sourceMap.size();
        }
    }


    /**
     * @param text
     * @param sub
     * @return
     */

    public static int count(String text, String sub) {
        int count = 0, start = 0;
        while ((start = text.indexOf(sub, start)) >= 0) {
            start += sub.length();
            count++;
        }
        return count;
    }
}