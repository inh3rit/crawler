/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import com.zxsoft.crawler.common.entity.sync.TextBlacklistEntity;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 施洋青
 * DATE： 16-10-27.、
 * <p/>
 * 文本黑名单过滤评分
 * 根据黑名单正则表达式过滤标题或者正文中是否包含，有则返回评分1.0 没有则返回评分0.0
 */
public class TextBlackListFilter extends FilterChainImpl {
    private static final String TITLE_TYPE = "title";
    private static final String CONTENT_TYPE = "content";
    private List<TextBlacklistEntity> textBlacklistEntityList;

    private double threshold = 0.0; // 阀值

    public TextBlackListFilter(List<TextBlacklistEntity> textBlacklistEntityList) {
        this.textBlacklistEntityList = textBlacklistEntityList;
        threshold = 0.51;
        addFilter(this);
    }

    public TextBlackListFilter(double threshold, List<TextBlacklistEntity> textBlacklistEntityList) {
        this.threshold = threshold;
        this.textBlacklistEntityList = textBlacklistEntityList;
        addFilter(this);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public double doFilter(final Map<String, Object> map) {
        if (textBlacklistEntityList == null || textBlacklistEntityList.size() <= 0)
            return 1.0;

        String title = (String) map.get(Filter.TITLE);
        String content = (String) map.get(Filter.CONTENT);

        // 文本黑名单过滤
        for (TextBlacklistEntity entity : textBlacklistEntityList) {
            if ((!title.isEmpty() && TITLE_TYPE.equals(entity.getType()) && isHave(title, entity.getRegex())) || (!content.isEmpty() && CONTENT_TYPE.equals(entity.getType()) && isHave(content, entity.getRegex()))) {
                return 0.0;
            }
        }
        return 1.0;
    }


    /**
     * @param str
     * @param regex
     * @return
     */
    private static boolean isHave(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

}