package com.zxsoft.crawler.p.ext.text.impl.BBS;

import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.ext.text.impl.BBSExtractor;
import com.zxsoft.crawler.p.model.RecordModel;

/**
 * Created by xiawenchao on 16-9-13.
 * VanillaBBS模板处理类
 *
 * @author xiawenchao
 * @version 1.0
 */
public class VanillaBBSExtractor extends BBSExtractor {

    private static final String KEY = "class";
    private static final String Value = "Message";

    /**
     * 静态内部类
     */
    private static class SingletonExtractor {
        //静态初始化器，由JVM来保证线程安全
        private static VanillaBBSExtractor instance = new VanillaBBSExtractor();
    }

    /**
     * 私有化构造方法
     */
    private VanillaBBSExtractor() {

    }

    /**
     * 获取操作对象单实例
     *
     * @return VanillaBBSExtractor对象
     */
    public static VanillaBBSExtractor getInstance() {
        return SingletonExtractor.instance;
    }

    @Override
    public String getContentByHtml(String html) throws Exception {
        RecordModel.dao.moduleCount(RecordType.MODULE_BBS_VanillaBBS, 1);
        return super.getContentByHtml(html, KEY, Value);
    }
}
