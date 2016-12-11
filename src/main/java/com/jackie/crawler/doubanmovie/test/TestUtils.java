package com.jackie.crawler.doubanmovie.test;

import org.junit.Test;

import java.io.File;

/**
 * Created by Jackie on 2016/9/24 0024.
 */
public class TestUtils {

    @Test
    public void testFile(){
        File seedFile = new File(this.getClass().getResource("/seed.properties").getPath());
        System.out.print("===========" + seedFile.length());
    }
}
