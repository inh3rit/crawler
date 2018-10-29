package com.zxsoft.crawler.common.type;

/**
 * 優化時間提取之初考慮替換特定字符, 但特話存在很多不確定性
 * 暫時不做使用
 */
@Deprecated
public enum TimeReplace {
    PUBLISH_IN("发布在", "TR_PUBLISH_IN"),
    PUBLISH("发表", "TR_PUBLISH"),
    RELEASE("发布", "TR_RELEASE"),
    TIME("时间", "TR_TIME"),
    SOURCE("来源", ""),
    REPLY("回复", ""),
    YEAR("年", ""),
    MONTH("月", ""),
    DAY("日", ""),
    HOUR_T("時", ""),
    HOUR_S("时", ""),
    MINUTE("分", ""),
    SECOND("秒", ""),
    BEFORE("前", ""),
    IN_S("于", ""),
    IN_T("於", ""),
    FLOOR_T("樓", ""),
    FLOOR_S("楼", "");
    private final String chinese;
    private final String replace;
    TimeReplace(String chinese, String replace) {
        this.chinese = chinese;
        this.replace = replace;
    }
    public String getChinese() {
        return this.chinese;
    }
    public String getReplace() {
        return this.replace;
    }
}
