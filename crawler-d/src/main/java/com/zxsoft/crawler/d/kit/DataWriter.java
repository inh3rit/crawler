package com.zxsoft.crawler.d.kit;

import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.out.WriterEntity;
import com.zxsoft.crawler.d.api.WriterDataApi;
import com.zxsoft.crawler.d.impl.solr.WriterSolrImpl;
import com.zxsoft.crawler.exception.CrawlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2016/1/11.
 */
public class DataWriter {

    private static final Logger log = LoggerFactory.getLogger(DataWriter.class);
    private static final WriterDataApi wda = new WriterSolrImpl();

    public static List<WriterEntity> write(String[] address, List<RecordInfoEntity> ries) {
        try {
            if (address == null || address.length == 0) return null;
            return wda.writer(address, ries);
        } catch (CrawlerException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
