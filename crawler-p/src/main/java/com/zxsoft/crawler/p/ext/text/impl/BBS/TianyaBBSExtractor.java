package com.zxsoft.crawler.p.ext.text.impl.BBS;

import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.ext.text.impl.BBSExtractor;
import com.zxsoft.crawler.p.model.RecordModel;

/**
 * 处理天涯论坛的提取器
 * Created by tony on 16-9-29.
 */
public class TianyaBBSExtractor extends BBSExtractor {

    private static final String KEY = "class";
    private static String _url = new String();
    private static String Value = new String();

    /**
     * 静态内部类
     */
    private static class SingletonExtractor {
        //静态初始化器，由JVM来保证线程安全
        private static TianyaBBSExtractor instance = new TianyaBBSExtractor();
    }

    /**
     * 私有化构造方法
     */
    private TianyaBBSExtractor() {

    }

    /**
     * 获取操作对象单实例
     *
     * @return TianyaBBSExtractor对象
     */
    public static TianyaBBSExtractor getInstance(String url) {
        _url = url;
        return SingletonExtractor.instance;
    }

    @Override
    public String getContentByHtml(String html) throws Exception {
        if (_url.contains("m.tianya")) {
            Value = "sp lk";
        }
        if (_url.contains("bbs.tianya/m")) {
            Value = "bd";
        }
        if (_url.contains("bbs.tianya")) {
            Value = "bbs-content clearfix";
        }
        RecordModel.dao.moduleCount(RecordType.MODULE_BBS_TIANYA, 1);
        return super.getContentByHtml(html, KEY, Value);
    }


}
