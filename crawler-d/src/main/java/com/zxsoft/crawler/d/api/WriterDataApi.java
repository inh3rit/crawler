package com.zxsoft.crawler.d.api;

import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.out.WriterEntity;
import com.zxsoft.crawler.exception.CrawlerException;

import java.util.List;

/**
 * Created by cox on 2015/10/13.
 */
public interface WriterDataApi {

    List<WriterEntity> writer(String[] postUrl, List<RecordInfoEntity> ries) throws CrawlerException;

}
