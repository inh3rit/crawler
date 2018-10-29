package com.zxsoft.crawler.s.test;

import org.junit.Test;

import java.io.File;

/**
 * Created by cox on 2015/12/18.
 */
public class testFile {


    @Test
    public void test() {
        File f = new File("src/main/resources/soubaocookie.txt");
        System.out.println(f.getAbsoluteFile());
        System.out.println(f.isFile());
    }


}
