package com.zxsoft.crawler.common.type;

/**
 * Created by cox on 2015/9/6.
 */
public enum JobType {
    UNKNOWN(0),
    NETWORK_SEARCH(1),
    NETWORK_INSPECT(2),
    NETWORK_FOCUS(3);

    private final int index;

    JobType(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public static JobType getIndex(Integer index) {
        for (JobType jt : JobType.values()) {
            if (jt.getIndex()!=index) continue;
            return jt;
        }
        return UNKNOWN;
    }

}
