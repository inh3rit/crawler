/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.strategy;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.cleaning.filter.CatalogPageScoreFilter;
import com.zxsoft.crawler.p.cleaning.filter.Filter;
import com.zxsoft.crawler.p.model.RecordModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 施洋青
 * DATE： 16-11-30.
 */
public class CatalogPageSourceStrategy {

    private static final Logger log = LoggerFactory.getLogger(CatalogPageSourceStrategy.class);
    private static CatalogPageSourceStrategy sourceStrategy;
    private Filter fc;
    private Prop conf;
    public static final String MAP_CODE = "code";
    public static final String MAP_MSG = "msg";

    private CatalogPageSourceStrategy() {
        /**
         * 获取配置文件信息
         */
        this.conf = PropKit.use("threshold.properties");
        this.fc = new CatalogPageScoreFilter(Double.parseDouble(this.conf.get("CatalogPageScoreFilter")));

    }

    public static CatalogPageSourceStrategy init() {
        if (sourceStrategy == null)
            sourceStrategy = new CatalogPageSourceStrategy();
        return sourceStrategy;
    }

    public Map<String, Object> start(final Map<String, Object> objectMap) {
        Map<String, Object> map = new HashMap<>();
        map.put(CatalogPageSourceStrategy.MAP_CODE, true);
        map.put(CatalogPageSourceStrategy.MAP_MSG, "ok");
        double r;
        try {
            for (Filter f : this.fc.filters) {
                if (f instanceof CatalogPageScoreFilter) {
                    r = f.doFilter(objectMap);
                    if (r > f.getThreshold()) {
                        map.put(CatalogPageSourceStrategy.MAP_CODE, false);
                        map.put(CatalogPageSourceStrategy.MAP_MSG, "该页面为列表页的可能性:" + r + ">" + f.getThreshold());

                        RecordModel.dao.moduleCount(RecordType.DATA_CLEANING_CATALOGPAGESCOREFILTER, 1);
                        return map;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return map;
    }
}