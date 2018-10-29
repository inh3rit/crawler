package com.zxsoft.crawler.p.ext.text.impl;

import com.zxsoft.crawler.p.ext.text.AbstractExtractor;

/**
 * Created by xiawenchao on 16-9-13.
 * VanillaBBS模板处理类
 *
 * @author xiawenchao
 * @version 1.0
 */
public class DefaultExtractor extends AbstractExtractor {
    /**
     * 静态内部类
     */
    private static class SingletonExtractor {
        //静态初始化器，由JVM来保证线程安全
        private static DefaultExtractor instance = new DefaultExtractor();
    }

    /**
     * 私有化构造方法
     */
    private DefaultExtractor() {

    }

    /**
     * 获取操作对象单实例
     *
     * @return DefaultBBSExtractor对象
     */
    public static DefaultExtractor getInstance() {
        return SingletonExtractor.instance;
    }
}
