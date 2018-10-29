package com.zxsoft.crawler.m;

import com.jfinal.kit.JsonKit;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cox on 2015/11/19.
 */
public class testList {

    @Test
    public void ttd1() {
        List<Double> l = new ArrayList<Double>();
        for (Integer i=100; i-->0;) {
            Double d = Math.random()*100;
            l.add(d);
        }
        Iterator<Double> it = l.iterator();
        while(it.hasNext()) {
            Double d = it.next();
            it.remove();
            System.out.println(d);
        }
        System.out.println(JsonKit.toJson(l));
    }

}
