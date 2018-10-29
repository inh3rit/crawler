package com.zxsoft.crawler.common.type;

/**
 * Created by cox on 2015/12/18.
 */
public enum PropType {
    UNKNOW(0, "未知类型"),
    TEXT(1, "纯文本类型"),
    NUMBER(2, "数值类型"),
    DOUBLE(3, "小数类型"),
    JSON(4, "JSON 格式数据"),
    BOOLEAN(5, "true|false"),
    IPV4(6, "IP 4 格式"),
    URL(7, "URL 格式数据"),
    JOB_TYPE(8, "1(全网搜索) 2(网络巡检) 3(重点关注)"),
    SYNC_TABLE(9, "数据库中存在的表名称");
    private final Integer index;
    private final String remark;
    PropType(Integer index, String remark) {
        this.index = index;
        this.remark = remark;
    }
    public Integer getIndex() {
        return this.index;
    }

    public String getRemark() {
        return remark;
    }

    public static PropType getType(Integer index) {
        for (PropType type : PropType.values()) {
            if (!index.equals(type.getIndex())) continue;
            return type;
        }
        return UNKNOW;
    }
}
