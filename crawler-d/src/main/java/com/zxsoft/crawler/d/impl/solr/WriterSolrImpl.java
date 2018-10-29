package com.zxsoft.crawler.d.impl.solr;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.out.WriterEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.d.api.WriterDataApi;
import com.zxsoft.crawler.exception.CrawlerException;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cox on 2015/10/13.
 */
public class WriterSolrImpl implements WriterDataApi {

    private static final Logger log = LoggerFactory.getLogger(WriterSolrImpl.class);

    @Override
    public List<WriterEntity> writer(String[] postUrl, List<RecordInfoEntity> ries) throws CrawlerException {
        if (postUrl == null || postUrl.length < 1 || ries == null || ries.isEmpty())
            throw new CrawlerException(CrawlerException.ErrorCode.OUTPUT_DATA_ERROR, "写入数据失败");
        if (CollectionKit.isEmpty(ries))
            throw new CrawlerException(CrawlerException.ErrorCode.OUTPUT_DATA_ERROR, "未发现需要写入的数据");
        String source_name = ries.get(0).getSourceName();
        String type = ries.get(0).getType();
        Record wr = new Record();
        wr.set("num", ries.size()).set("records", ries);
        String json = JsonKit.toJson(wr);
        List<WriterEntity> lwe = new ArrayList<>();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpConst.CONTENT_TYPE, "application/json");
        for (String url : postUrl) {
            WriterEntity we = new WriterEntity();
            we.setUrl(url);
            if (StrKit.isBlank(url)) continue;
            HttpEntity he = HttpKit.post(url, null, json, headers, Charset.forName("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (Integer i = 0; i < ries.size(); i++)
                sb.append(ries.get(i).getId()).append(i + 1 == ries.size() ? "" : ",");
            if (he.getResponseCode() >= 200 && he.getResponseCode() < 300) {
                we.setSuccess(ries.size()).setMessage(he.getHtml());
                log.debug("write to Solr success, {}-{}, [{}]", source_name, type, sb.toString());
            } else {
                we.setFail(ries.size()).setDataFail(ries).setMessage(he.getHtml());
                log.debug("write to Solr error, {}-{}, [{}]", source_name, type, sb.toString());
            }
            lwe.add(we);
        }
        return lwe;
    }
}
