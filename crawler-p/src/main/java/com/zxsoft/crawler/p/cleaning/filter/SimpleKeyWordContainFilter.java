/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import java.util.List;
import java.util.Map;

/**
 * Created by 施洋青
 * DATE： 16-9-20.
 * <p/>
 * 简单的关键字包含性评分
 * 判定方式，通过判定A组关键字是否包含在B组关键字中，关键字包含的数量除以A组关键字总数量得到A组关键字的包含性评分
 * 返回的分值越大则包含性越高 最大1.0 最小0.0
 */
public class SimpleKeyWordContainFilter extends FilterChainImpl {

    private double threshold = 0.0;

    public SimpleKeyWordContainFilter() {
        threshold = 0.3;
        addFilter(this);
    }

    public SimpleKeyWordContainFilter(double threshold) {
        this.threshold = threshold;
        addFilter(this);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public double doFilter(final Map<String, Object> map) {
        List<String> smallGroupKeyWord = (List<String>) map.get(Filter.KEYWORD);
        List<String> bigGroupKeyWord = (List<String>) map.get(Filter.CONTENT_KEYWORD);

        //只要有一个文本为null，规定包含度分值为0，表示完全不相等
        if (smallGroupKeyWord == null || bigGroupKeyWord == null)
            return 0.0;

        //如果一个文本为空，另一个不为空，规定包含度分值为0，表示完全不相等
        if (smallGroupKeyWord.isEmpty() || bigGroupKeyWord.isEmpty())
            return 0.0;

        //如果两个文本都为空，规定包含度分值为1，表示完全相等
        if (smallGroupKeyWord.isEmpty() && bigGroupKeyWord.isEmpty())
            return 1.0;

        //如果bigGroupKeyWord完全包含smallGroupKeyWord，规定包含度分值为1，表示完全相等
        if (bigGroupKeyWord.containsAll(smallGroupKeyWord))
            return 1.0;

        // 查看smallGroupKeyWord 关键字词组在bigGroupKeyWord 中包含数量
        int count = 0;
        int smallGroupKeyWordLength = smallGroupKeyWord.size();
        for (String ss : smallGroupKeyWord) {
           /* for (String bs : bigGroupKeyWord) {
                if (bs.contains(ss.trim()))
                    count++;
            }*/
            if (bigGroupKeyWord.toString().contains(ss.trim()))
                count++;

        }
        double score = count / (double) smallGroupKeyWordLength;
        score = (int) (score * 1000000 + 0.5) / (double) 1000000;
        return score;
    }
}