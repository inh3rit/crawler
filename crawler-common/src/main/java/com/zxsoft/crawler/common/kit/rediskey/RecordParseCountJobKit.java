package com.zxsoft.crawler.common.kit.rediskey;

import com.zxsoft.crawler.common.entity.redis.RecordParseCountEntity;

/**
 * Created by cox on 2015/11/30.
 */
public class RecordParseCountJobKit {

    public static RecordParseCountEntity resolve(String str) {
        RecordParseCountEntity re = new RecordParseCountEntity();
        String[] cs = str.split("\\^");
        re.setTime(cs[0]);
        re.setKey(Integer.parseInt(cs[1]));
        re.setCount(Long.parseLong(cs[2]));
        return re;
    }

    public static String serialize(RecordParseCountEntity re) {
        StringBuilder sb = new StringBuilder();
        sb.append(re.getTime()).append("^").append(re.getKey()).append("^").append(re.getCount());
        return sb.toString();
    }

}
