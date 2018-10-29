package org.beetl.ext.fn;

import com.jfinal.kit.StrKit;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * Created by cox on 2015/8/31.
 * 2016-01-04
 * 因增加 cw_type 表, 为了统一, 这个函数应不再使用
 */
@Deprecated
public class JobtypeFunction implements Function {


    private static final String[] JOBTYPE = {"未知", "全网搜索", "网络巡检", "重点关注"};

    @Override
    public Object call(Object[] objects, Context context) {
        if (objects.length<1) return "未知";
        String t = objects[0].toString();
        Integer type = Integer.valueOf(StrKit.isBlank(t) ? "0" : t);
        return JOBTYPE[type];
    }
}
