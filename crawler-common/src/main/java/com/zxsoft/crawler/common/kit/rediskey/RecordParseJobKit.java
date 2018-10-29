package com.zxsoft.crawler.common.kit.rediskey;

import com.zxsoft.crawler.common.entity.redis.RecordParseEntity;

/**
 * Created by cox on 2015/11/30.
 */
public class RecordParseJobKit {

    public static RecordParseEntity resolve(String str) {
        RecordParseEntity re = new RecordParseEntity();
        String[] cs = str.split("\\^");
        re.setKey(Integer.parseInt(cs[0]));
        re.setValue(cs[1]);
        return re;
    }

    public static String serialize(RecordParseEntity re) {
        StringBuilder sb = new StringBuilder();
        sb.append(re.getKey()).append("^").append(re.getValue());
        return sb.toString();
    }

}
