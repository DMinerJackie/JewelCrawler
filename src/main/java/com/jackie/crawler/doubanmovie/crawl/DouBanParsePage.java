package com.jackie.crawler.doubanmovie.crawl;

/**
 * Created by Jackie on 2016/9/24 0024.
 */

import com.jackie.crawler.doubanmovie.constants.Constants;
import com.jackie.crawler.doubanmovie.entity.Comments;
import com.jackie.crawler.doubanmovie.entity.Movie;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DouBanParsePage {

    protected static Movie movie;
    protected static Comments comments;

    public static int movieId = 0;
    public static int commentId = 0;

    public static void parseFromString(String content, Connection conn) throws Exception {

        Parser parser = new Parser(content);
        HasAttributeFilter filter = new HasAttributeFilter("href");

        String sql1 = null;
        ResultSet rs1 = null;
        PreparedStatement pstmt1 = null;
        Statement stmt1 = null;

        List<String> nextLinkList = new ArrayList<String>();

        int rowCount = 0;
        sql1 = "select count(*) as rowCount from record";
        stmt1 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        rs1 = stmt1.executeQuery(sql1);
        if (rs1.next()) {
            rowCount = rs1.getString("rowCount") != null ? Integer.parseInt(rs1.getString("rowCount")) : 0;
        }

        if (rowCount <= Constants.maxCycle) { //once rowCount is bigger than maxCycle, the new crawled link will not insert into record table
            try {
                NodeList list = parser.parse(filter);
                int count = list.size();

                //process every link on this page
                for (int i = 0; i < count; i++) {
                    Node node = list.elementAt(i);

                    if (node instanceof LinkTag) {
                        LinkTag link = (LinkTag) node;
                        String nextLink = link.extractLink();
                        String mainUrl = Constants.MAINURL;

                        if (nextLink.startsWith(mainUrl)) {
                                //check if the link already exists in the database
                                sql1 = "SELECT * FROM record WHERE URL = '" + nextLink + "'";
                                stmt1 = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                                rs1 = stmt1.executeQuery(sql1);
                                if (rs1.next()) {

                                } else {
                                    Pattern moviePattern = Pattern.compile(Constants.MOVIE_REGULAR_EXP);
                                    Matcher movieMatcher = moviePattern.matcher(nextLink);

                                    Pattern commentPattern = Pattern.compile(Constants.COMMENT_REGULAR_EXP);
                                    Matcher commentMatcher = commentPattern.matcher(nextLink);

                                    if (movieMatcher.find() || commentMatcher.find()) {
                                        nextLinkList.add(nextLink);
                                    }
                                }
                        }
                    }
                }
                if (nextLinkList.size() > 0) {
                    conn.setAutoCommit(false);
                    //if the link does not exist in the database, insert it
                    sql1 = "INSERT INTO record (URL, crawled) VALUES (?,0)";
                    pstmt1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
                    for (String nextLinkStr : nextLinkList) {
                        pstmt1.setString(1, nextLinkStr);
                        pstmt1.addBatch();
                        System.out.println(nextLinkStr);
                    }
                    pstmt1.executeBatch();
                    conn.commit();
                }
            } catch (Exception e) {
                //handle the exceptions
                e.printStackTrace();
                System.out.println("SQLException: " + e.getMessage());
            } finally {
                //close and release the resources of PreparedStatement, ResultSet and Statement
                if (pstmt1 != null) {
                    try {
                        pstmt1.close();
                    } catch (SQLException e2) {
                    }
                }
                pstmt1 = null;

                if (rs1 != null) {
                    try {
                        rs1.close();
                    } catch (SQLException e1) {
                    }
                }
                rs1 = null;

                if (stmt1 != null) {
                    try {
                        stmt1.close();
                    } catch (SQLException e3) {
                    }
                }
                stmt1 = null;
            }
        }
    }

    public static void extractMovie(String url, String content, Connection conn) {
        System.out.println("==========Parse Movie:" + url + "============");
        Document doc = Jsoup.parse(content);
        String sql = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        //parse movie detail page
        Pattern moviePattern = Pattern.compile(Constants.MOVIE_REGULAR_EXP);
        Matcher movieMatcher = moviePattern.matcher(url);
        if (movieMatcher.find()) {
            Document movieDoc = Jsoup.parse(content);
            //if the page is movie detail page, then parse the page
            if (movieDoc.html().contains("导演") && movieDoc.html().contains("主演") && movieDoc.html().contains("类型") &&
                    movieDoc.html().contains("语言") && doc.getElementById("info") != null) {
//                String name = movieDoc.getElementById("content").getElementsByAttributeValue("property", "v:itemreviewed").text();
                Elements infos = doc.getElementById("info").children();
                movie = new Movie(movieId++);
                for (Element info : infos) {
                    if (info.childNodeSize() > 0) {
                        String key = info.getElementsByAttributeValue("class", "pl").text();
                        if ("导演".equals(key)) {
                            movie.setDirector(info.getElementsByAttributeValue("class", "attrs").text());
                        } else if ("编剧".equals(key)) {
                            movie.setScenarist(info.getElementsByAttributeValue("class", "attrs").text());
                        } else if ("主演".equals(key)) {
                            movie.setActors(info.getElementsByAttributeValue("class", "attrs").text());
                        } else if ("类型:".equals(key)) {
                            movie.setType(doc.getElementsByAttributeValue("property", "v:genre").text());
                        } else if ("制片国家/地区:".equals(key)) {
                            Pattern patternCountry = Pattern.compile(".制片国家/地区:</span>.+[\\u4e00-\\u9fa5]+.+[\\u4e00-\\u9fa5]+\\s+<br>");
                            Matcher matcherCountry = patternCountry.matcher(doc.html());
                            if (matcherCountry.find()) {
                                movie.setCountry(matcherCountry.group().split("</span>")[1].split("<br>")[0].trim());// for example: >制片国家/地区:</span> 中国大陆 / 香港     <br>
                            }
                        } else if ("语言:".equals(key)) {
                            Pattern patternLanguage = Pattern.compile(".语言:</span>.+[\\u4e00-\\u9fa5]+.+[\\u4e00-\\u9fa5]+\\s+<br>");
                            Matcher matcherLanguage = patternLanguage.matcher(doc.html());
                            if (matcherLanguage.find()) {
                                movie.setLanguage(matcherLanguage.group().split("</span>")[1].split("<br>")[0].trim());
                            }
                        } else if ("上映日期:".equals(key)) {
                            movie.setReleaseDate(doc.getElementsByAttributeValue("property", "v:initialReleaseDate").text());
                        } else if ("片长:".equals(key)) {
                            movie.setRuntime(doc.getElementsByAttributeValue("property", "v:runtime").text());
                        }
                    }
                }
                movie.setTags(doc.getElementsByClass("tags-body").text());
                movie.setName(doc.getElementsByAttributeValue("property", "v:itemreviewed").text());
                movie.setRatingNum(doc.getElementsByAttributeValue("property", "v:average").text());
            }
            //add if condition for resume crawling for example the last crawl was stopped, and now resume crawling, the method extractMovie was executed, but movie was null, it will throw null exception
            if (movie != null) {
                //save the movie record
                try {
                    sql = "SELECT * FROM movie WHERE name = '" + movie.getName() + "'";
                    stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                    rs = stmt.executeQuery(sql);

                    if (rs.next()) {
                        System.out.println(movie.getName() + "is already existed!!!");
                    } else {
                        //if the link does not exist in the database, insert it
                        sql = "INSERT INTO movie (name, director, scenarist, actors, type, country, language, releaseDate, runtime, ratingNum, tags) VALUES " +
                                "('" + movie.getName() + "','" + movie.getDirector() + "', '" + movie.getScenarist() + "', '" + movie.getActors() + "'" +
                                ", '" + movie.getType() + "', '" + movie.getCountry() + "', '" + movie.getLanguage() + "', '" + movie.getReleaseDate() + "'" +
                                ", '" + movie.getRuntime() + "', '" + movie.getRatingNum() + "', '" + movie.getTags() + "')";
                        pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        pstmt.execute();
                        System.out.println(">>>>>>saving " + movie.getName() + "<<<<<<");
                    }
                } catch (SQLException e) {
                    //handle the exceptions
                    System.out.println("SQLException: " + e.getMessage());
                    System.out.println("SQLState: " + e.getSQLState());
                    System.out.println("VendorError: " + e.getErrorCode());
                } finally {
                    //close and release the resources of PreparedStatement, ResultSet and Statement
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                        } catch (SQLException e2) {
                        }
                    }
                    pstmt = null;

                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e1) {
                        }
                    }
                    rs = null;

                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e3) {
                        }
                    }
                    stmt = null;
                }
            }
        }
    }

    public static void extractComment(String url, String content, Connection conn) {
        System.out.println("==========Parse Comment:" + url + "============");
        String sql = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        //parse comment page
        Pattern commentPattern = Pattern.compile(Constants.COMMENT_REGULAR_EXP);
        Matcher commentMatcher = commentPattern.matcher(url);
        if (commentMatcher.find()) {
            Document commentDoc = Jsoup.parse(content);
            if (commentDoc.getElementById("comments") != null) { // add to avoid exception like https://movie.douban.com/subject/25842478/comments
                Elements commentsElements = commentDoc.getElementById("comments").children();
                comments = new Comments(commentId++);
                for (Element comment : commentsElements) {
                    if (comment.getElementsByClass("fold-bd").size() < 1 && comment.children().get(1).getElementsByTag("p").size() > 0) {
                        // to make sure the current item is the comment item rather than other info item      &&      检测fold-bd是查看是否有折叠，如果是折叠的评论则有fold-bd，折叠评论是指账号有异常的
                        String[] movies = commentDoc.getElementsByTag("h1").text().replace(" ", "").split("短评");
                        String commentForMovie = null;
                        for (String movie : movies) {
                            commentForMovie = movie;
                        }
                        comments.setCommentForMovie(commentForMovie);
                        comments.setCommentInfo(comment.children().get(1).getElementsByTag("p").text());//use "comment.children().get(1).text()" can get all commentInfo like "1819 有用 桃桃淘电影 2016-10-29 即便评分再高也完全喜欢不来。我们还是太热衷主题与意义了，以至于忽视了传递主题的方式与合理性。影片为了所谓的人性深度，而刻意设计剧情和人物转折，忽视基本的人物行为轨迹，都非常让人不舒服。喜欢有深度的电影，但希望能以更巧妙的方式讲出来，而不该是现在这样。以及形式上，这不就是舞台搬演么"
                        if (comment.getElementsByAttributeValue("class", "votes pr5").text().length() > 0) {
                            comments.setCommentVote(Integer.parseInt(comment.getElementsByAttributeValue("class", "votes pr5").text()));
                        }
                        comments.setCommentAuthor(comment.getElementsByAttribute("href").get(2).text());
                        comments.setCommentAuthorImgUrl(comment.getElementsByAttribute("href").get(2).attr("href"));

                        //save comment record
                        try {
                            sql = "SELECT * FROM comments WHERE commentInfo = '" + comments.getCommentInfo() + "'";
                            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                            rs = stmt.executeQuery(sql);

                            if (rs.next()) {
                                System.out.println(comments.getCommentAuthor() + "'s comment has been saved!!!");
                            } else {
                                //if the link does not exist in the database, insert it
                                sql = "INSERT INTO comments (commentInfo, commentAuthor, commentAuthorImgUrl, commentVote, commentForMovie) VALUES " +
                                        "('" + comments.getCommentInfo() + "','" + comments.getCommentAuthor() + "', '" + comments.getCommentAuthorImgUrl() + "'" +
                                        ", '" + comments.getCommentVote() + "', '" + comments.getCommentForMovie() + "')";
                                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                pstmt.execute();
                                System.out.println(">>>>>>saving comment for " + comments.getCommentForMovie() + " by:" + comments.getCommentAuthor() + "<<<<<<");
                            }
                        } catch (SQLException e) {
                            //handle the exceptions
                            System.out.println("SQLException: " + e.getMessage());
                            System.out.println("SQLState: " + e.getSQLState());
                            System.out.println("VendorError: " + e.getErrorCode());
                        } finally {
                            //close and release the resources of PreparedStatement, ResultSet and Statement
                            if (pstmt != null) {
                                try {
                                    pstmt.close();
                                } catch (SQLException e2) {
                                }
                            }
                            pstmt = null;

                            if (rs != null) {
                                try {
                                    rs.close();
                                } catch (SQLException e1) {
                                }
                            }
                            rs = null;

                            if (stmt != null) {
                                try {
                                    stmt.close();
                                } catch (SQLException e3) {
                                }
                            }
                            stmt = null;
                        }
                    }

                }
            }
        }
    }
}
