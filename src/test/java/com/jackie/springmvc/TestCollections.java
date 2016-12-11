package com.jackie.springmvc;

import com.jackie.crawler.doubanmovie.entity.Comments;
import com.jackie.crawler.doubanmovie.utils.DBUtils;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.print.Doc;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jackie on 2016/10/16 0016.
 */

public class TestCollections {

    @Test
    public void testRandom(){
        double x=(Math.random() + 1);
        int number = new Random().nextInt(5) + 1;
        System.out.println("x:" + x);
        System.out.println("number:" + number);
    }
}
