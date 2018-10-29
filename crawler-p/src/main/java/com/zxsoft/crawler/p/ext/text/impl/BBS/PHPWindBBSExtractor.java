package com.zxsoft.crawler.p.ext.text.impl.BBS;

import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.ext.text.impl.BBSExtractor;
import com.zxsoft.crawler.p.model.RecordModel;

/**
 * Created by xiawenchao on 16-9-13.
 * PHPWindBBS论坛模板处理类
 *
 * @author xiawenchao
 * @version 1.0
 */
public class PHPWindBBSExtractor extends BBSExtractor {

    private static final String KEY = "class";
    private static final String Value = "editor_content";

    /**
     * 静态内部类
     */
    private static class SingletonExtractor {
        //静态初始化器，由JVM来保证线程安全
        private static PHPWindBBSExtractor instance = new PHPWindBBSExtractor();
    }

    /**
     * 私有化构造方法
     */
    private PHPWindBBSExtractor() {

    }

    /**
     * 获取操作对象单实例
     *
     * @return PHPWindBBSExtractor对象
     */
    public static PHPWindBBSExtractor getInstance() {
        return SingletonExtractor.instance;
    }

    @Override
    public String getContentByHtml(String html) throws Exception {
        RecordModel.dao.moduleCount(RecordType.MODULE_BBS_PHPWind, 1);
        return super.getContentByHtml(html, KEY, Value);
    }
}
