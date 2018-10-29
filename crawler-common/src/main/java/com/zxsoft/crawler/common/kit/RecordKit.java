package com.zxsoft.crawler.common.kit;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by iaceob on 2015/11/17.
 */
public class RecordKit {

    private static final Logger log = LoggerFactory.getLogger(RecordKit.class);

    private static String[] sortStrings(String[] words) {
        String tmp;
        Collator collator = Collator.getInstance(new Locale("en", "US"));
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if (collator.compare(words[i], words[j]) > 0) {
                    tmp = words[i];
                    words[i] = words[j];
                    words[j] = tmp;
                }
            }
        }
        return words;
    }

    @Deprecated
    public static Record sort(Record oriRecord) {
        String[] keys = RecordKit.sortStrings(oriRecord.getColumnNames());
        Record r = new Record();
        for (String key : keys) {
            r.set(key, oriRecord.get(key));
        }
        return r;
    }

    /**
     * 对 Record 进行有序的转化为 json 字符串,
     * 目前仅一层排序, 若需要多层排序, 须对 ori.get(key)
     * 判断类型递归类似 toJsonSort 方法
     * @param ori
     * @return
     */
    public static String toJsonSort(Record ori) {
        StringBuilder sb = new StringBuilder();
        String[] keys = RecordKit.sortStrings(ori.getColumnNames());
        sb.append("{");
        for (Integer i=0; i<keys.length; i++) {
            String key = keys[i];
            sb.append("\"").append(key).append("\"")
                    .append(":").append(JsonKit.toJson(ori.get(key)))
                    .append(i+1==keys.length ? "" : ",");
        }
        sb.append("}");
        return sb.toString();
    }

}
