package com.jackie.crawler.doubanmovie.crawl;

/**
 * Created by Jackie on 2016/9/24 0024.
 */

import com.jackie.crawler.doubanmovie.constants.Constants;
import com.jackie.crawler.doubanmovie.utils.DBUtils;
import com.jackie.crawler.doubanmovie.utils.LoadSeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 1. load and read seed file
 * 2. connect mysql database
 */
public class DouBanCrawler extends DouBanParsePage {
    public static final Logger log = LoggerFactory.getLogger(DouBanCrawler.class);

    public static void main(String args[]) throws Exception {

        //load and read seed file
        List<String> seedList = LoadSeed.loadSeed();
        if (seedList == null) {
            log.info("No seed to crawl, please check again");
            return;
        }
        String frontPage = seedList.get(0);

        //connect database mysql
        Connection conn = DBUtils.connectDB();

        //create tables to store crawled data
        DBUtils.createTables();

        String sql = null;
        String url = frontPage;
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        List<String> urlList = new ArrayList<String>();
        urlList.add(url);

        //crawl every link in the database
        while (true) {
            //get page content of link "url"
            DouBanHttpGetUtil.getByString(urlList, conn);
            count++;

            //set boolean value "crawled" to true after crawling this page

            //TODO batch update
            int result = 0;
            conn.setAutoCommit(true);
            for (String urlStr : urlList) {
                sql = "UPDATE record SET crawled = 1 WHERE URL = '" + urlStr + "'";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);
            }

            urlList.clear();//empty for every loop
            if (stmt.executeUpdate(sql) > 0  || frontPage.equals(url)) {
                //get the next page that has not been crawled yet
                sql = "SELECT * FROM record WHERE crawled = 0 limit 10";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    url = rs.getString(2);
                    urlList.add(url);
                }
                //set a limit of crawling count
                if (rs.next() || count > Constants.maxCycle || url == null) {
                    break;
                }
            }
        }
        conn.close();
        conn = null;

        System.out.println("Done.");
        System.out.println(count);
    }
}
