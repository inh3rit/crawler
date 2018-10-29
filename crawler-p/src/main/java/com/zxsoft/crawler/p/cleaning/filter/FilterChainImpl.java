/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import java.util.Map;
import java.util.Set;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 */
public abstract class FilterChainImpl implements Filter {

    /**
     * 添加过滤实现
     *
     * @param filter
     * @return
     */
    @Override
    public Filter addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    /**
     * 获取过滤器集合实现
     *
     * @return
     */
    @Override
    public Set<Filter> getFilters() {
        return this.filters;
    }

    public abstract double getThreshold();

    public abstract double doFilter(final Map<String, Object> map);
}