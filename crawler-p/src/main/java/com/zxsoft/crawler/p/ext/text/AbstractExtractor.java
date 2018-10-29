package com.zxsoft.crawler.p.ext.text;

import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.ext.ContentExtractor;
import com.zxsoft.crawler.p.model.RecordModel;

/**
 * Created by xiawenchao on 16-9-13.
 *
 *
 */
public abstract class AbstractExtractor implements IExtractor {

    @Override
    public String getContentByHtml(String html) throws Exception {
        RecordModel.dao.moduleCount(RecordType.MODULE_DEFAULT, 1);
        return ContentExtractor.getContentByHtml(html);
    }
}
