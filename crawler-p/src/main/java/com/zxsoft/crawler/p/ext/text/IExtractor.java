package com.zxsoft.crawler.p.ext.text;

/**
 * Created by xiawenchao on 16-9-13.
 * BBS正文获取接口
 * @author xwc
 * @version 1.0
 */
public interface IExtractor {
    /**
     * @param html 页面html
     * @return 获取正文内容
     */
    public String getContentByHtml(String html) throws Exception;
}
