package com.jackie.crawler.doubanmovie.entity;

import org.springframework.stereotype.Component;

/**
 * Created by Jackie on 2016/9/24 0024.
 */
@Component
public class Comments {
    private int commentId;
    private String commentInfo;
    private String commentAuthor;
    private String commentAuthorImgUrl;
    private int commentVote;

    private String commentForMovie;

    private int recordId;
    public Comments(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(String commentInfo) {
        this.commentInfo = commentInfo;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getCommentAuthorImgUrl() {
        return commentAuthorImgUrl;
    }

    public void setCommentAuthorImgUrl(String commentAuthorImgUrl) {
        this.commentAuthorImgUrl = commentAuthorImgUrl;
    }

    public int getCommentVote() {
        return commentVote;
    }

    public void setCommentVote(int commentVote) {
        this.commentVote = commentVote;
    }

    public String getCommentForMovie() {
        return commentForMovie;
    }

    public void setCommentForMovie(String commentForMovie) {
        this.commentForMovie = commentForMovie;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "commentId=" + commentId +
                ", commentInfo='" + commentInfo + '\'' +
                ", commentAuthor='" + commentAuthor + '\'' +
                ", commentAuthorImgUrl='" + commentAuthorImgUrl + '\'' +
                ", commentVote=" + commentVote +
                ", commentForMovie='" + commentForMovie + '\'' +
                ", recordId=" + recordId +
                '}';
    }
}
