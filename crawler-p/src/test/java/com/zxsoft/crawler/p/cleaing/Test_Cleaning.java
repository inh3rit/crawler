/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaing;

import org.junit.Test;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 */
public class Test_Cleaning {


    @Test
    public void test() {

        String keyword = "肥东       test          拆迁";
        String title = "张静专家好吗_合肥仁爱中医医院张静医生治疗技术好不好_医讯问答_hfrakp";
        String content = "原标题：六安看性病哪个医院好 2016-09-27 11:59:55 阜阳最好的尖锐湿疣医院是哪家,阜阳治尖锐湿疣哪家好,铜陵哪里治疗性病比较好,铜陵治疗男性性病哪里好,铜陵治疗性病,合肥治疗生殖";

//        List<String> words = Arrays.asList(keyword.split("\\s+"));
//        List<String> words1 = KeyWordComputerByNlpChina.computeArticleTfidf(title, null);
//        List<String> words2 = KeyWordComputerByNlpChina.computeArticleTfidf(title, content, null);
//
//        System.out.println(words);
//        System.out.println(words1);
//        System.out.println(words2);

//        Map<String, Object> map = DefaultStrategy.process.start(words, words1, words2);
//        System.out.println(map.get(DefaultStrategy.MAP_CODE).toString());
//        System.out.println(map.get(DefaultStrategy.MAP_MSG).toString());


//        try {
//            if (!new AnalyticalNetworkSearchDefault().dataCleaning(keyword, title, content, null)) {
//                throw new CrawlerException(CrawlerException.ErrorCode.DATA_CLEANING, " The [xxx] data is removed by data cleaning.");
//            } else {
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}