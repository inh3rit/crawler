/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.common.type;

/**
 * Created by 施洋青
 * DATE： 16-8-24.
 */
public enum RecordType {
    MODULE_DEFAULT(10000, "默认模板"),
    MODULE_NEWS(11000, "新闻模板"),
    MODULE_BLOG(12000, "博客模板"),
    MODULE_BBS(13000, "论坛模板"),
    MODULE_BBS_OTHER(13001, "论坛模板-其他"),
    MODULE_BBS_Discuz(13002, "论坛模板-Discuz"),
    MODULE_BBS_PHPWind(13003, "论坛模板-PHPWind"),
    MODULE_BBS_VanillaBBS(13004, "论坛模板-VanillaBBS"),
    MODULE_BBS_TIANYA(13005, "论坛模板-天涯"),
    /*后续增加论坛其他模块需要在此处增加枚举类型数据，均已13xxx写法，方便统计*/

    COUNT_SUM(20000, "数量统计-总量"),
    COUNT_BLACK_LIST(21000, "数量统计-黑名单"),
    COUNT_WHITE_LIST(22000, "数量统计-白名单"),
    COUNT_NO_RULE(23000, "数量统计-没有规则"),
    COUNT_HAVE_RULE_HAVE_RESULT(24001, "数量统计-有规则且有结果"),
    COUNT_HAVE_RULE_NO_RESULT(24002, "数量统计-有规则但无结果"),
    COUNT_BBS_OTHER(25000, "数量统计-论坛模板-其他"),

    DATA_CLEANING_SUM(30000, "数据清洗-总量"),
    DATA_CLEANING_SENSITIVEKEYWORDFILTER(31000, "数据清洗-敏感词库过滤"),
    DATA_CLEANING_SIMPLEKEYWORDSIMILARITYFILTER(32000, "数据清洗-关键词组相似过滤"),
    DATA_CLEANING_SIMPLEKEYWORDCONTAINFILTER(33000, "数据清洗-关键词组包含性过滤"),
    DATA_CLEANING_TEXTBLACKLISTFILTER(34000, "数据清洗-关键词组包含性过滤"),
    DATA_CLEANING_CATALOGPAGESCOREFILTER(35000, "数据清洗-网页为列表页过滤");

    private final Integer index;
    private final String remark;

    RecordType(Integer index, String remark) {
        this.index = index;
        this.remark = remark;
    }

    public Integer getIndex() {
        return index;
    }

    public String getRemark() {
        return remark;
    }
}