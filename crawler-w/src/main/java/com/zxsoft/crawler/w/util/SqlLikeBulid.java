package com.zxsoft.crawler.w.util;

import com.jfinal.kit.StrKit;

import java.util.Map;
import java.util.Set;

/**
 * Created by cox on 2015/8/17.
 */
public class SqlLikeBulid {

    private static final String[] RELATION = {"and", "or"};
    private String[] allows;

    public SqlLikeBulid(String ... allows) {
        this.allows = allows;
    }

    private String parasLike(String key, String[] vals, Boolean last, Integer relation) {
        if (this.allows==null||this.allows.length==0) return "";
        Boolean exist = false;
        for(Integer i=this.allows.length; i-->0;) {
            if (key.toUpperCase().equals(this.allows[i].toUpperCase())) {
                exist = true;
                break;
            }
        }
        if (!exist) return "";
        StringBuilder sb = new StringBuilder();
        if (vals.length==1) {
            sb.append(" ").append(key).append(" like '%").append(vals[0]).append("%' ");
            if (!last) sb.append(RELATION[relation]);
            return sb.toString();
        }
        sb.append(" (");
        for (Integer i=0; i<vals.length; i++) {
            String val = vals[i];
            if (StrKit.isBlank(val)) continue;
            sb.append(" ").append(key).append(" like '%").append(val).append("%' ");
            sb.append(i==vals.length-1 ? "" : RELATION[1]);
        }
        sb.append(") ").append(RELATION[relation]).append(" ");
        return sb.toString();
    }

    public String bulid(Map<String, String[]> paras, Integer relation) {
        if (paras.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        // sb.append(" 1=1 ").append(RELATION[relation]);
        Set<String> set = paras.keySet();
        Integer ix = 0;
        for(String key : set) {
            ix+=1;
            String[] vals = paras.get(key);
            sb.append(parasLike(key, vals, set.size()==ix, relation));
        }
        return sb.toString();
    }

    public String bulid(Map<String, String[]> paras) {
        return this.bulid(paras, 0);
    }

}
