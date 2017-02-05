package com.jackie.crawler.doubanmovie.constants;

/**
 * Created by Jackie on 2016/9/25 0025.
 */
public class Constants {
    public static String MAINURL = "https://movie.douban.com/";
    public static int maxCycle = 150000;
    public static final String ENCODING = "UTF-8";
    public static final String BLANKSPACE = " ";
    public static final String MOVIE_REGULAR_EXP = "https://movie.douban.com/subject/\\d{8}";
    public static final String COMMENT_REGULAR_EXP = "https://movie.douban.com/subject/\\d{8}/comments";
}
