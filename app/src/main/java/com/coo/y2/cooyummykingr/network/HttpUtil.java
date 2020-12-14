package com.coo.y2.cooyummykingr.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Map;

/**
 * Created by Y2 on 2015-04-25.
 */
public class HttpUtil {
    private static AsyncHttpClient client;
    private static AsyncHttpClient getClient() {
        if (client == null) {
            client = new AsyncHttpClient();
            client.setTimeout(20000);
        }
        return client;
    }

    public static void get(String url, Map<String, String> headers, RequestParams params, AsyncHttpResponseHandler handler) {
        setHeaders(headers);
//        getClient().get(getAbsoluteUrl(url), params, handler);
        getClient().get(url, params, handler);
    }

    public static void post(String url, Map<String, String> headers, RequestParams params, AsyncHttpResponseHandler handler) {
        setHeaders(headers);
//        getClient().post(getAbsoluteUrl(url), params, handler);
        getClient().post(url, params, handler);
    }

    public static void cancle() {
        getClient().cancelAllRequests(true);
    }

    public static void setHeaders(Map<String, String> headers) {
        if (headers == null) {
            return;
        }

        for(Map.Entry<String, String> entry : headers.entrySet()) {
            getClient().addHeader(entry.getKey(), entry.getValue());
        }
    }
//    private static String getAbsoluteUrl(String relativeUrl) {
//        return URL.getBaseUrl() + relativeUrl;
//    }
}
