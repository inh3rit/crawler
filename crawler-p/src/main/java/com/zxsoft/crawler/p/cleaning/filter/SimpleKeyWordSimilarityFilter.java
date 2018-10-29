/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 * <p/>
 * 简单的关键字相似度评分
 * 判定方式：简单共有词，通过计算两篇文档共有的词的总字符数除以最长文档字符数来评估他们的相似度
 * 返回的评分越大相似度越高 最大为1.0 最小0.0
 */
public class SimpleKeyWordSimilarityFilter extends FilterChainImpl {
    private double threshold = 0.0;

    public SimpleKeyWordSimilarityFilter() {
        this.threshold = 0.45;
        addFilter(this);
    }

    public SimpleKeyWordSimilarityFilter(double threshold) {
        this.threshold = threshold;
        addFilter(this);
    }

    /**
     * 获取过滤器阀值实现
     *
     * @return
     */
    @Override
    public double getThreshold() {
        return threshold;
    }


    /**
     * 过滤器方法实现
     *
     * @param map
     * @return
     */
    @Override
    public double doFilter(final Map<String, Object> map) {

        List<String> words1 = (List<String>) map.get(Filter.TITLE_KEYWORD);
        List<String> words2 = (List<String>) map.get(Filter.CONTENT_KEYWORD);

        if (words1 == null || words2 == null) {
            //只要有一个文本为null，规定相似度分值为0，表示完全不相等
            return 0.0;
        }
        if (words1.isEmpty() || words2.isEmpty()) {
            //如果一个文本为空，另一个不为空，规定相似度分值为0，表示完全不相等
            return 0.0;
        }
        if (words1.isEmpty() && words2.isEmpty()) {
            //如果两个文本都为空，规定相似度分值为1，表示完全相等
            return 1.0;
        }
        //计算词列表1总的字符数
        int words1Length = 0;
        for (String s : words1)
            words1Length += s.length();
        //计算词列表2总的字符数
        int words2Length = 0;
        for (String s : words2)
            words2Length += s.length();
        //计算词列表1和词列表2共有的词的总的字符数

        List<String> intersection = new ArrayList<>(words1);
        intersection.retainAll(words2);
        int intersectionLength = 0;
        for (String s : intersection)
            intersectionLength += s.length();
        double score = intersectionLength / (double) Math.max(words1Length, words2Length);
        //System.out.println("词列表1总的字符数：" + words1Length);
        //System.out.println("词列表2总的字符数：" + words2Length);
        //System.out.println("词列表1和2共有的词的总的字符数：" + intersectionLength);
        //System.out.println("相似度分值=" + intersectionLength + "/(double)Math.max(" + words1Length + ", " + words2Length + ")=" + score);
        score = (int) (score * 1000000 + 0.5) / (double) 1000000;
        //System.out.println("取六位小数，四舍五入，分值：" + score);
        return score;
    }
}