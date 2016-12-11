package com.jackie.crawler.doubanmovie.utils;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by Jackie on 2016/9/24 0024.
 */
public class DBUtils {

    public static final Logger log = LoggerFactory.getLogger(DBUtils.class);
    public static java.sql.Connection conn = null;

    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin";

    public static Connection connectDB() throws Exception {
        try {
            ApplicationContext act=new ClassPathXmlApplicationContext("beans.xml");
            BasicDataSource dataSource = (BasicDataSource) act.getBean("dataSource");
            Class.forName("com.mysql.jdbc.Driver");
            String dburl = dataSource.getUrl();
            conn = DriverManager.getConnection(dburl, dataSource.getUsername(), dataSource.getPassword());
            log.info("connection built");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return conn;
    }

    public static void createTables() {
        String sql = null;
        Statement stmt = null;

        if (conn != null) {
            //create database and table that will be needed
            try {
                sql = "CREATE DATABASE IF NOT EXISTS douban_crawler";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);

                sql = "USE douban_crawler";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);

                //create table record
                sql = "create table if not exists record (recordId int(5) not null auto_increment, URL text not null, crawled tinyint(1) not null, primary key (recordId)) engine=InnoDB DEFAULT CHARSET=utf8";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);

                //create table movie
                sql = "create table if not exists movie (movieId int(4) not null auto_increment, director text not null, scenarist text not null,actors text not null,type text not null,country text not null,language text not null, releaseDate text not null,runtime text not null,ratingNum text not null, primary key (movieId)) engine=InnoDB DEFAULT CHARSET=utf8";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);

                //create table comments
                sql = "create table if not exists comments (commentId int(4) not null auto_increment, commentInfo text not null, commentAuthor text not null,commentAuthorImgUrl text not null,commentVote text not null,recordId text not null, primary key (commentId)) engine=InnoDB DEFAULT CHARSET=utf8";
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
