package com.jackie.crawler.doubanmovie.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Jackie on 2016/9/24 0024.
 *
 * load the file contain seeds to crawl
 * return the seed list
 */
public class LoadSeed {
    public static List<String> loadSeed() throws IOException {
        File seedFile = new File(LoadSeed.class.getResource("/seed.properties").getPath());
        System.out.print("===========" + seedFile.length());
        return FileUtils.readLines(seedFile);
    }
}
