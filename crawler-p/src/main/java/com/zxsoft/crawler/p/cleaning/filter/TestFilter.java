/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import java.util.Map;

/**
 * Created by 施洋青
 * DATE： 16-9-19.
 * <p/> test
 */
public class TestFilter extends FilterChainImpl {
    private double threshold = 0.0;

    public TestFilter() {
        threshold = 0.0;
        addFilter(this);
    }

    public TestFilter(double threshold) {
        this.threshold = threshold;
        addFilter(this);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public double doFilter(final Map<String, Object> objectMap) {
        return 0.0;
    }
}