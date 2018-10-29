package com.zxsoft.crawler.common.type;

/**
 * Created by cox on 2016/1/8.
 */
public enum FocusStat {
    UNKNOW(0, "未知"),
    NORMAL(1, "正常"),
    UNUSUAL(2, "异常"),
    WAIT(3, "等待");
    private final Integer index;
    private final String remark;
    FocusStat(Integer index, String remark) {
        this.index = index;
        this.remark = remark;
    }

    public Integer getIndex() {
        return index;
    }

    public String getRemark() {
        return remark;
    }

    public static FocusStat getStat(Integer index) {
        for (FocusStat stat : FocusStat.values()) {
            if (!index.equals(stat.getIndex())) continue;
            return stat;
        }
        return UNKNOW;
    }
}
