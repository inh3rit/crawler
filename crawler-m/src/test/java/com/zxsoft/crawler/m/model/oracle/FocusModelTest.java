package com.zxsoft.crawler.m.model.oracle;

import com.jfinal.kit.JsonKit;
import org.junit.Test;

/**
 * Created by cox on 2016/1/9.
 */
public class FocusModelTest {

    @Test
    public void testArraycopy1() {
        Integer first = 2;
        String[] array = new String[]{"a", "b", "c"};
        Object[] os = new Object[array.length+1];
        os[0] = first;
        for (Integer i=0; i<array.length; i++)
            os[i+1] = array[i];
        System.out.println(JsonKit.toJson(os));
    }

    @Test
    public void testArraycopy2() {
        Integer first = 2;
        String[] array = new String[]{"a", "b", "c"};
        Object[] os = new Object[array.length+1];
        os[0] = first;
        System.arraycopy(array, 0, os, 1, array.length);
        System.out.println(JsonKit.toJson(os));
    }


}