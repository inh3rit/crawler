/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.strategy;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.zxsoft.crawler.common.entity.sync.SensitiveKeyWordEntity;
import com.zxsoft.crawler.common.entity.sync.TextBlacklistEntity;
import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.p.cleaning.filter.*;
import com.zxsoft.crawler.p.model.RecordModel;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 */
public class DefaultStrategy {
    private static final Logger log = LoggerFactory.getLogger(DefaultStrategy.class);
    private static DefaultStrategy strategy;
    private Filter fc;
    private Prop conf;

    public static final String MAP_CODE = "code";
    public static final String MAP_MSG = "msg";

    private DefaultStrategy() {
    }

    /**
     * @param skws                  敏感词词库数组
     * @param textBlacklistEntities 文本黑名单正则表达式数组
     */
    private DefaultStrategy(SensitiveKeyWordEntity[] skws, TextBlacklistEntity[] textBlacklistEntities) {
        /**
         * 获取配置文件信息
         */
        this.conf = PropKit.use("threshold.properties");

        /**
         * 参数转换1
         */
        List<String> sensitiveWord = new ArrayList<>();
        if (skws != null && skws.length > 0)
            for (int i = 0; i < skws.length; i++)
                sensitiveWord.add(skws[i].getKwd());
        /**
         * 参数转换2
         */
        List<TextBlacklistEntity> textBlacklistEntityList = new ArrayList<>();
        if (textBlacklistEntities != null && textBlacklistEntities.length > 0)
            for (int i = 0; i < textBlacklistEntities.length; i++)
                textBlacklistEntityList.add(textBlacklistEntities[i]);

        /**
         * 添加过滤器，过滤器会按照添加先后顺序执行
         */
        this.fc = new SensitiveKeyWordFilter(Double.parseDouble(this.conf.get("SensitiveKeyWordFilter")), sensitiveWord)
                .addFilter(new SimpleKeyWordSimilarityFilter(Double.parseDouble(this.conf.get("SimpleKeyWordSimilarityFilter"))))
                .addFilter(new SimpleKeyWordContainFilter(Double.parseDouble(this.conf.get("SimpleKeyWordContainFilter"))))
                .addFilter(new TextBlackListFilter(Double.parseDouble(this.conf.get("TextBlackListFilter")), textBlacklistEntityList));

        log.debug("过滤器初始化成功,当前过滤器数量:{}", this.fc.filters.size());
    }

    /**
     * 初始化
     *
     * @param skws                  敏感词词库数组
     * @param textBlacklistEntities 文本黑名单正则表达式数组
     * @return
     */
    public static DefaultStrategy init(SensitiveKeyWordEntity[] skws, TextBlacklistEntity[] textBlacklistEntities) {
        if (strategy == null)
            strategy = new DefaultStrategy(skws, textBlacklistEntities);
        return strategy;
    }


    /**
     * 过滤器策略执行
     *
     * @param objectMap
     * @return
     */
    public Map<String, Object> start(final Map<String, Object> objectMap) {
        double r;
        Map<String, Object> map = new HashMap<>();
        map.put(DefaultStrategy.MAP_CODE, true);
        map.put(DefaultStrategy.MAP_MSG, "ok");

        List<String> keyWord = (List<String>) objectMap.get(Filter.KEYWORD);
        List<String> keyWord_title = (List<String>) objectMap.get(Filter.TITLE_KEYWORD);
        List<String> keyWord_content = (List<String>) objectMap.get(Filter.CONTENT_KEYWORD);
        String title = (String) objectMap.get(Filter.TITLE);
        String content = (String) objectMap.get(Filter.CONTENT);
        try {
            RecordModel.dao.moduleCount(RecordType.DATA_CLEANING_SUM, 1);

            for (Filter f : this.fc.filters) {
                if (f instanceof SensitiveKeyWordFilter) {
                    r = f.doFilter(objectMap);
                    //log.debug("数据清洗-敏感词排除评分:{},阀值:{}", r, f.getThreshold());
                    if (r < f.getThreshold()) {
                        map.put(DefaultStrategy.MAP_CODE, false);
                        map.put(DefaultStrategy.MAP_MSG, keyWord_title + "关键字词组不是敏感词的可能性为:" + r + "<" + f.getThreshold());

                        RecordModel.dao.moduleCount(RecordType.DATA_CLEANING_SENSITIVEKEYWORDFILTER, 1);
                        return map;
                    }
                } else if (f instanceof SimpleKeyWordContainFilter) {
                    r = f.doFilter(objectMap);
                    //log.debug("数据清洗-关键字词库包含性评分:{},阀值:{}", r, f.getThreshold());
                    if (r < f.getThreshold()) {
                        map.put(DefaultStrategy.MAP_CODE, false);
                        map.put(DefaultStrategy.MAP_MSG, keyWord + "与" + keyWord_content + "两组关键字词组包含性评分:" + r + "<" + f.getThreshold());

                        RecordModel.dao.moduleCount(RecordType.DATA_CLEANING_SIMPLEKEYWORDCONTAINFILTER, 1);
                        return map;
                    }
                } else if (f instanceof SimpleKeyWordSimilarityFilter) {
                    r = f.doFilter(objectMap);
                    //log.debug("数据清洗-关键字词库相似性评分:{},阀值:{}", r, f.getThreshold());
                    if (r < f.getThreshold()) {
                        map.put(DefaultStrategy.MAP_CODE, false);
                        map.put(DefaultStrategy.MAP_MSG, keyWord_title + "与" + keyWord_content + "两组关键字词组相似性评分:" + r + "<" + f.getThreshold());

                        RecordModel.dao.moduleCount(RecordType.DATA_CLEANING_SIMPLEKEYWORDSIMILARITYFILTER, 1);
                        return map;
                    }
                } else if (f instanceof TextBlackListFilter) {
                    r = f.doFilter(objectMap);
                    //log.debug("数据清洗-文本黑名单评分:{},阀值:{},title:{},filters:{}", r, f.getThreshold(), title, this.fc.filters.size());
                    if (r < f.getThreshold()) {
                        map.put(DefaultStrategy.MAP_CODE, false);
                        map.put(DefaultStrategy.MAP_MSG, "文本黑名单过滤器评分:" + r + "<" + f.getThreshold() + ",标题:" + title);

                        RecordModel.dao.moduleCount(RecordType.DATA_CLEANING_TEXTBLACKLISTFILTER, 1);
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