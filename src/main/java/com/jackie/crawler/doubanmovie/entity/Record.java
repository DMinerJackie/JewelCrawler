package com.jackie.crawler.doubanmovie.entity;

import org.springframework.stereotype.Component;

/**
 * Created by Jackie on 2016/9/24 0024.
 */
@Component
public class Record {
    private int recordId;
    private String URL;
    private int crawled;

    public Record(int recordId) {
        this.recordId = recordId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getCrawled() {
        return crawled;
    }

    public void setCrawled(int crawled) {
        this.crawled = crawled;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "Record{" +
                "recordId=" + recordId +
                ", URL='" + URL + '\'' +
                ", crawled=" + crawled +
                '}';
    }
}
