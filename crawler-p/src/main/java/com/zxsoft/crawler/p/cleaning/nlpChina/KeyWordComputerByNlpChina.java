/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.nlpChina;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 */
public class KeyWordComputerByNlpChina {

    private static KeyWordComputer kwc = new KeyWordComputer(10); // 最好固定为10,经测试10个关键字效果为佳

    public static List<String> computeArticleTfidf(String title, String content, Integer nKeyword) {
        setKwc(nKeyword);
        Collection<Keyword> result = kwc.computeArticleTfidf(title, content);
        List<String> words = new ArrayList<>();
        for (Keyword k : result)
            words.add(k.getName());
        return words;
    }

    public static List<String> computeArticleTfidf(String text, Integer nKeyword) {
        setKwc(nKeyword);
        Collection<Keyword> result = kwc.computeArticleTfidf(text);
        List<String> words = new ArrayList<>();
        for (Keyword k : result)
            words.add(k.getName());
        return words;
    }

    private static void setKwc(Integer nKeyword) {
        if (nKeyword != null && nKeyword > 0)
            kwc = new KeyWordComputer(nKeyword);
    }
}