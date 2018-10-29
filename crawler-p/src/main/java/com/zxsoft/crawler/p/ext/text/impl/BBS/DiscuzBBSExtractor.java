package com.zxsoft.crawler.p.ext.text.impl.BBS;

import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.ext.text.impl.BBSExtractor;
import com.zxsoft.crawler.p.model.RecordModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by xiawenchao on 16-9-13.
 * Discuz模板BBS信息获取实现类
 *
 * @author xiawenchao
 * @version 1.0
 */
public class DiscuzBBSExtractor extends BBSExtractor {

    private static final String KEY = "class";
    private static final String Value = "t_f";

    /**
     * 私有化构造方法
     */
    private DiscuzBBSExtractor() {
    }

    /**
     * 静态内部类构造单例
     */
    private static class SingletonExtractor {
        //静态初始化器，由JVM来保证线程安全
        private static DiscuzBBSExtractor instance = new DiscuzBBSExtractor();
    }

    /**
     * 获取单实例对象
     *
     * @return DiscuzBBSExtractor对象
     */
    public static DiscuzBBSExtractor getInstance() {
        return SingletonExtractor.instance;
    }

    @Override
    public String getContentByHtml(String html) throws Exception {
        RecordModel.dao.moduleCount(RecordType.MODULE_BBS_Discuz, 1);
        return super.getContentByHtml(html, KEY, Value);
    }

    public String getDateByHtml(String html) throws Exception {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("p.hm").select("span");

        return "";
    }

}
