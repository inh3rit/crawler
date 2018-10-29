package com.zxsoft.crawler.w.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by cox on 2015/9/2.
 */
public class JsonKit extends com.jfinal.kit.JsonKit {

    private static final Logger log = LoggerFactory.getLogger(JsonKit.class);

    private static <T> T objectToJson(JSONObject jo) throws JSONException {
        Record record = new Record();
        Set<String> set = jo.keySet();
        for(String key : set) {
            Object type = jo.get(key);

            if (type instanceof JSONArray) {
                record.set(key, arrayToJson(jo.getJSONArray(key)));
                continue;
            }
            if (type instanceof JSONObject) {
                record.set(key, objectToJson(jo.getJSONObject(key)));
                continue;
            }
            if (type instanceof String) {
                record.set(key, jo.getString(key));
                continue;
            }
            if (type instanceof Short) {
                record.set(key, jo.getShort(key));
                continue;
            }
            if (type instanceof Integer) {
                record.set(key, jo.getInteger(key));
                continue;
            }
            if (type instanceof Long) {
                record.set(key, jo.getLong(key));
                continue;
            }
            if (type instanceof Float) {
                record.set(key, jo.getFloat(key));
                continue;
            }
            if (type instanceof Double) {
                record.set(key, jo.getDouble(key));
                continue;
            }
            if (type instanceof BigDecimal) {
                record.set(key, jo.getBigDecimal(key));
                continue;
            }
            if (type instanceof Date) {
                record.set(key, jo.getDate(key));
                continue;
            }
            if (type instanceof Timestamp) {
                record.set(key, jo.getTimestamp(key));
                continue;
            }
            if (type instanceof Boolean) {
                record.set(key, jo.getBoolean(key));
                continue;
            }
            if (type instanceof Byte) {
                record.set(key, jo.getByte(key));
                continue;
            }
            if (type instanceof java.sql.Date) {
                record.set(key, jo.getSqlDate(key));
                break;
            }
            record.set(key, jo.get(key));
        }
        return (T)record;
    }

    private static List arrayToJson(JSONArray ja) throws JSONException {
        List list = new ArrayList<>();
        for(Integer i=0; i<ja.size(); i++) {
            Object o = ja.get(i);
            if (o instanceof JSONArray) {
                list.add(arrayToJson(ja.getJSONArray(i)));
                continue;
            }
            if (o instanceof JSONObject) {
                list.add(objectToJson(ja.getJSONObject(i)));
                continue;
            }
            if (o instanceof String) {
                list.add(ja.getString(i));
                continue;
            }
            if (o instanceof Short) {
                list.add(ja.getShort(i));
                continue;
            }
            if (o instanceof Integer) {
                list.add(ja.getInteger(i));
                continue;
            }
            if (o instanceof Long) {
                list.add(ja.getLong(i));
                continue;
            }
            if (o instanceof Float) {
                list.add(ja.getFloat(i));
                continue;
            }
            if (o instanceof Double) {
                list.add(ja.getDouble(i));
                continue;
            }
            if (o instanceof BigDecimal) {
                list.add(ja.getBigDecimal(i));
                continue;
            }
            if (o instanceof Date) {
                list.add(ja.getDate(i));
                continue;
            }
            if (o instanceof Timestamp) {
                list.add(ja.getTimestamp(i));
                continue;
            }
            if (o instanceof Boolean) {
                list.add(ja.add(ja.getBoolean(i)));
                continue;
            }
            if (o instanceof Byte) {
                list.add(ja.add(ja.getByte(i)));
                continue;
            }
            if (o instanceof java.sql.Date) {
                list.add(ja.add(ja.getSqlDate(i)));
                break;
            }
            list.add(ja.get(i));

        }
        return list;
    }

    /**
     * json 字符串反序列化
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<? extends Record> clazz) {
        // TODO 目前仅适用于 jfinal Record 对象的反序列换, 若需要支持更多类型尚需完善
        try {
            JSONObject jo = JSON.parseObject(json);
            Record record = null;
            try {
                record = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Set<String> set = jo.keySet();
            for(String key : set) {
                Object type = jo.get(key);

                if (type instanceof JSONArray) {
                    record.set(key, arrayToJson(jo.getJSONArray(key)));
                    continue;
                }
                if (type instanceof JSONObject) {
                    record.set(key, objectToJson(jo.getJSONObject(key)));
                    continue;
                }
                if (type instanceof String) {
                    record.set(key, jo.getString(key));
                    continue;
                }
                if (type instanceof Short) {
                    record.set(key, jo.getShort(key));
                    continue;
                }
                if (type instanceof Integer) {
                    record.set(key, jo.getInteger(key));
                    continue;
                }
                if (type instanceof Long) {
                    record.set(key, jo.getLong(key));
                    continue;
                }
                if (type instanceof Float) {
                    record.set(key, jo.getFloat(key));
                    continue;
                }
                if (type instanceof Double) {
                    record.set(key, jo.getDouble(key));
                    continue;
                }
                if (type instanceof BigDecimal) {
                    record.set(key, jo.getBigDecimal(key));
                    continue;
                }
                if (type instanceof Date) {
                    record.set(key, jo.getDate(key));
                    continue;
                }
                if (type instanceof Timestamp) {
                    record.set(key, jo.getTimestamp(key));
                    continue;
                }
                if (type instanceof Boolean) {
                    record.set(key, jo.getBoolean(key));
                    continue;
                }
                if (type instanceof Byte) {
                    record.set(key, jo.getByte(key));
                    continue;
                }
                if (type instanceof java.sql.Date) {
                    record.set(key, jo.getSqlDate(key));
                    break;
                }
                record.set(key, jo.get(key));
            }
            return (T)record;
        } catch (JSONException e) {
            log.error("JSON 数据格式错误, 转换失败", e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }



}
