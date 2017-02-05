package com.jackie.crawler.doubanmovie.crawl;

/**
 * Created by Jackie on 2016/9/24 0024.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Random;

public class DouBanHttpGetUtil {

    public final static void getByString(List<String> urlList, Connection conn) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            for (String url : urlList) {
                    HttpGet httpGet = new HttpGet(url);
                    System.out.println("executing request " + httpGet.getURI());

                    ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                        public String handleResponse(
                                final HttpResponse response) throws ClientProtocolException, IOException {
                            int status = response.getStatusLine().getStatusCode();
                            System.out.println("------------status:" + status);
                            if (status >= 200 && status < 300) {
                                HttpEntity entity = response.getEntity();
                                return entity != null ? EntityUtils.toString(entity) : null;
                            } else if (status == 300 || status ==301 || status == 302 ||status == 304 || status == 400 ||
                                    status == 401 || status == 403 || status == 404 || new String(status + "").startsWith("5")){ //refer to link http://blog.csdn.net/u012043391/article/details/51069441
                                return null;
                            }else {
                                throw new ClientProtocolException("Unexpected response status: " + status);
                            }
                        }
                    };

                    friendlyToDouban();
                    String responseBody = httpClient.execute(httpGet, responseHandler);

                    if (responseBody != null) {
                        DouBanParsePage.parseFromString(responseBody, conn);//analyze all links and save into DB
                        DouBanParsePage.extractMovie(url, responseBody, conn);//analyze the page and save into DB if the current page is movie detail page
                        DouBanParsePage.extractComment(url, responseBody, conn);//analyze the page and save into DB if the current page is comment detail page
                    }
            }
        } finally {
            httpClient.close();
        }
    }

    private static void friendlyToDouban() throws InterruptedException {
        Thread.sleep((new Random().nextInt(10) + 1)*1000);//sleep for the random second so that avoiding to be listed into blacklist
    }
}
