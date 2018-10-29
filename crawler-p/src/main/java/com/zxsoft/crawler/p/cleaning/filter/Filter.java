/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 */
public interface Filter {
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String KEYWORD = "keyWord";
    public static final String TITLE_KEYWORD = "title_k";
    public static final String CONTENT_KEYWORD = "content_k";
    public static final String URL = "url";
    public static final String HTML = "html";

    /**
     * 过滤器集合
     */
    public Set<Filter> filters = new HashSet<>();

    /**
     * 获取过滤器阀值
     *
     * @return
     */
    double getThreshold();

    /**
     * 获取过滤器集合
     *
     * @return
     */
    Set<Filter> getFilters();

    /**
     * 添加过滤器
     *
     * @param filter
     * @return
     */
    Filter addFilter(Filter filter);

    /**
     * 过滤实现
     *
     * @param map
     * @return
     */
    double doFilter(final Map<String, Object> map);
}